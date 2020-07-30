(ns sfblog.core
  (:require
   [reagent.core :as reagent :refer [atom]]
   [reagent.dom :as rdom]
   [reagent.session :as session]
   [reitit.frontend :as reitit]
   [clerk.core :as clerk]
   [accountant.core :as accountant]))

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

(defn spacepost [postclass]
   [:div.single-col-post
    [:h1 "A Story About Space"]
    [:p "Never in all their history have men been able truly to conceive of the world as one: a single sphere, a globe, having the qualities of a globe, a round earth in which all the directions eventually meet, in which there is no center because every point, or none, is center an equal earth which all men occupy as equals. The airman's earth, if free men make it, will be truly round: a globe in practice, not in theory."]
    [:p "Science cuts two ways, of course; its products can be used for both good and evil. But there's no turning back from science. The early warnings about technological dangers also come from science."]
    [:p "What was most significant about the lunar voyage was not that man set foot on the Moon but that they set eye on the earth."]
    [:p "A Chinese tale tells of some men sent to harm a young girl who, upon seeing her beauty, become her protectors rather than her violators. That's how I felt seeing the Earth for the first time. I could not help but love and cherish her."]
    [:p "For those who have seen the Earth from space, and for the hundreds and perhaps thousands more who will, the experience most certainly changes your perspective. The things that we share in our world are far more valuable than those which divide us."]
    [:h2 {:class "section-heading"} "The Final Frontier"]
    [:p "There can be no thought of finishing for aiming for the stars. Both figuratively and literally, it is a task to occupy the generations. And no matter how much progress one makes, there is always the thrill of just beginning."]
    [:p "There can be no thought of finishing for aiming for the stars. Both figuratively and literally, it is a task to occupy the generations. And no matter how much progress one makes, there is always the thrill of just beginning."]
    [:blockquote {:class "blockquote"} "The dreams of yesterday are the hopes of today and the reality of tomorrow. Science has not yet mastered prophecy. We predict too much for the next year and yet far too little for the next ten."]
    [:p "Spaceflights cannot be stopped. This is not the work of any one man or even a group of men. It is a historical process which mankind is carrying out in accordance with the natural laws of human development."]
    [:h2 {:class "section-heading"} "Reaching for the Stars"]
    [:p "As we got further and further away, it (the Earth) diminished in size. Finally it shrank to the size of a marble, the most beautiful you can imagine. That beautiful, warm, living object looked so fragile, so delicate, that if you touched it with a finger it would crumble and fall apart. Seeing this has to change a man."]
    [:a {:href "#"}
     [:img {:class "img-fluid", :src "images/post-sample-image.jpg" }]]
    [:span {:class "caption text-muted"} "To go places and do things that have never been done before - that's what living is all about."]
    [:p "Space, the final frontier. These are the voyages of the Starship Enterprise. Its five-year mission: to explore strange new worlds, to seek out new life and new civilizations, to boldly go where no man has gone before."]
    [:p "As I stand out here in the wonders of the unknown at Hadley, I sort of realize there's a fundamental truth to our nature, Man must explore, and this is exploration at its greatest."]])

(defn single-column-post [postfunc]
  [:div.row
   [:div {:class "col-lg-8 col-md-10 mx-auto post-text"}
    [postfunc "single-col-post"]]])

;; -------------------------
;; Page components

(defn home-page []
  (fn []
    [:span.main
     [:div.container
      (single-column-post spacepost)]]))

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
