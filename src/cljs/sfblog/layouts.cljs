(ns sfblog.layouts)

(defn single-column-post [postfunc]
  [:div.row
   [:div {:class "col-lg-8 col-md-10 mx-auto post-text"}
    [postfunc "single-col-post"]]])

(defn two-column-post [postfunc]
  [:div.two-column
   [:div {:class "post post-text"}
    [postfunc "two-col-post"]]
   [:div.sidebar]])

