(ns sfblog.layouts)

(defn single-column-post [postfunc]
  [:div.row
   [:div {:class "col-lg-8 col-md-10 mx-auto post-text"}
    [postfunc "single-col-post"]]])

(defn two-column-post [postfunc]
  [:div.two-column
   [:div.post
    [postfunc "single-col-post"]]
   [:div.sidebar])

