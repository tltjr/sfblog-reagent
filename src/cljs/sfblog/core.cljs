(ns sfblog.core
  (:require
   [reagent.core :as reagent :refer [atom]]
   [reagent.dom :as rdom]
   [reagent.session :as session]
   [reitit.frontend :as reitit]
   [clerk.core :as clerk]
   [accountant.core :as accountant]
   [sfblog.layouts :as layouts]
   [sfblog.posts :as posts]))

;; -------------------------
;; Routes

(def router
  (reitit/router
   [["/" :index]
    ["/items"
     ["" :items]
     ["/:item-id" :item]]
    ["/about" :about]]))

(defn path-for [route & [params]]
  (if params
    (:path (reitit/match-by-name router route params))
    (:path (reitit/match-by-name router route))))

;; -------------------------
;; Page components

(defn home-page []
  (fn []
    [:span.main
     [:div.container
      (layouts/two-column-post posts/spacepost)]]))

;; (defn home-page []
;;   (fn []
;;     [:span.main
;;      [:div.container
;;       (single-column-post spacepost)]]))

(defn items-page []
  (fn []
    [:span.main
     [:h1 "The items of sfblog"]
     [:ul (map (fn [item-id]
                 [:li {:name (str "item-" item-id) :key (str "item-" item-id)}
                  [:a {:href (path-for :item {:item-id item-id})} "Item: " item-id]])
               (range 1 60))]]))


(defn item-page []
  (fn []
    (let [routing-data (session/get :route)
          item (get-in routing-data [:route-params :item-id])]
      [:span.main
       [:h1 (str "Item " item " of sfblog")]
       [:p [:a {:href (path-for :items)} "Back to the list of items"]]])))


(defn about-page []
  (fn [] [:span.main
          [:h1 "About sfblog"]]))


;; -------------------------
;; Translate routes -> page components

(defn page-for [route]
  (case route
    :index #'home-page
    :about #'about-page
    :items #'items-page
    :item #'item-page))


;; -------------------------
;; Page mounting component

(defn current-page []
  (fn []
    (let [page (:current-page (session/get :route))]
      [:div
        [:nav {:id "nav"}
         [:nav {:class "navbar navbar-expand-lg navbar-dark"}
          [:a {:class "navbar-brand", :href "#"}
           [:i {:class "fab fa-delicious"}] " Sparkling Fill"]
          [:button {:class "navbar-toggler", :type "button", :data-toggle "collapse", :data-target "#navbarNav", :aria-controls "navbarNav", :aria-expanded "false", :aria-label "Toggle navigation"}
           [:span {:class "navbar-toggler-icon"}]]
          [:div {:class "collapse navbar-collapse", :id "navbarNav"}
           [:div {:class "navbar-nav"}
            [:a {:class "nav-item nav-link active", :href "#"} "Main"]
            [:a {:class "nav-item nav-link active", :href "#"} "Feedback"]
            [:a {:class "nav-item nav-link active", :href "#"} "Go Pro"]
            [:a {:class "nav-item nav-link active", :href "https://docs.sparklingfill.com"} "Docs"]
            [:a {:class "nav-item nav-link active mobile-only", :href "#"} "New crossword"]
            [:a {:class "nav-item nav-link active mobile-only", :href "#"} "Your crosswords"]
            [:a {:class "nav-item nav-link active mobile-only", :href "#"} "Settings"]
            [:a {:class "nav-item nav-link active mobile-only", :href "#"} "Account"]
            [:a {:class "nav-item nav-link active mobile-only", :href "#"} "Help"]
            [:a {:class "nav-item nav-link active mobile-only", :href "#"} "Sign out"]]
           [:div {:class "navbar-nav ml-auto desktop-only"}
            [:div {:class "nav-item dropdown"}
             [:a {:class "nav-link dropdown-toggle", :href "#", :id "navbarDropdownMenuLink", :role "button", :data-toggle "dropdown", :aria-haspopup "true", :aria-expanded "false"} "Menu"]
             [:div {:class "dropdown-menu", :aria-labelledby "navbarDropdownMenuLink"}
              [:a {:class "dropdown-item", :href "#"} "New crossword"]
              [:a {:class "dropdown-item", :href "#"} "Your crosswords"]
              [:a {:class "dropdown-item", :href "#"} "Settings"]
              [:a {:class "dropdown-item", :href "#"} "Go Pro"]
              [:a {:class "dropdown-item", :href "#"} "Account"]
              [:a {:class "dropdown-item", :href "#"} "Help"]
              [:a {:class "dropdown-item", :href "#"} "Sign out"]]]]]]]
       [page]
       [:footer]])))

;; -------------------------
;; Initialize app

(defn mount-root []
  (rdom/render [current-page] (.getElementById js/document "app")))

(defn init! []
  (clerk/initialize!)
  (accountant/configure-navigation!
   {:nav-handler
    (fn [path]
      (let [match (reitit/match-by-path router path)
            current-page (:name (:data  match))
            route-params (:path-params match)]
        (reagent/after-render clerk/after-render!)
        (session/put! :route {:current-page (page-for current-page)
                              :route-params route-params})
        (clerk/navigate-page! path)
        ))
    :path-exists?
    (fn [path]
      (boolean (reitit/match-by-path router path)))})
  (accountant/dispatch-current!)
  (mount-root))
