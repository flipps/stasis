(ns nota.ui
  (:require [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
            [com.fulcrologic.fulcro.dom :as dom]
            [com.fulcrologic.fulcro.routing.dynamic-routing :as dr :refer [defrouter]]
            [nota.ui.pages :as ui.pages]
            [nota.ui.posts :as ui.posts]
            [nota.ui.posts.pagination :as ui.posts.pagination]
            [nota.routing :as routing]))

(defrouter TopRouter [_this {:keys [current-state]}]
  {:router-targets [ui.pages/Page
                    ui.posts/Post
                    ui.posts/ListPostByTag
                    ui.posts.pagination/PaginatedPosts]}
  (case current-state
    (nil :pending) (dom/div "Loading...")
    :failed (dom/div "Loading seems to have failed. Try another route.")
    (dom/div "Unknown route")))

(def ui-top-router (comp/factory TopRouter))

(defsc Header [_this {:keys [list-pages]}]
  {}
  (dom/header
   (dom/nav
    (map ui.pages/ui-list-page list-pages)
    (dom/button {:onClick #(routing/route-to! (dr/path-to ui.posts.pagination/PaginatedPosts "list"))}
                "Blog")
    (dom/button :.float-right
                {:onClick #(js/window.open "https://github.com/rafaeldelboni/nota" "_blank")}
                "Source"))))

(def header (comp/factory Header))

(defsc Footer [_this _]
  {}
  (dom/footer
   (dom/div
    (dom/hr)
    (dom/span "© 2021 built using ")
    (dom/a {:href "https://github.com/rafaeldelboni/nota"} "nota"))))

(def footer (comp/factory Footer))

(defsc Root [_this {:keys [list-pages]
                    :root/keys [router]}]
  {:query         [{:list-pages (comp/get-query ui.pages/ListPage)}
                   {:root/router (comp/get-query TopRouter)}]
   :initial-state {:root/router {}}}
  (dom/div
   (header {:list-pages list-pages})
   (dom/section
     (ui-top-router router))
   (footer)))
