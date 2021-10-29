(ns stasis.routing
  (:require [clojure.string :as str]
            [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
            [stasis.application.env :as env]
            [stasis.routing.pushy :as pushy]))

(defonce history (atom nil))

(defn route-to!
  "Change routes to the given route-string"
  [route-string]
  (let [app-prefix (:app-prefix env/config)]
    (pushy/set-token! @history (->> route-string
                                  (into [app-prefix])
                                  (remove str/blank?)
                                  (str/join "/")))))

(defn create-history [app]
  (let [{:keys [app-prefix path-prefix default-route]} env/config]
    (pushy/pushy (fn [path]
                 (let [real-path (if (= (first path) "/")
                                   path
                                   (str "/" path))
                       route-segments (->> (str/split real-path "/")
                                           (remove str/blank?)
                                           (remove #(= % path-prefix))
                                           (remove #(= % app-prefix))
                                           vec)]
                   (if (or (empty? route-segments)
                           (= [path-prefix] route-segments))
                     (route-to! [default-route])
                     (dr/change-route app route-segments))))
               identity
               :path-prefix (when-not (str/blank? path-prefix)
                              (str "/" path-prefix "/")))))

(defn start! [app]
  (pushy/start! (reset! history (create-history app))))
