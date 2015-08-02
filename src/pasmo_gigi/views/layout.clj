(ns pasmo-gigi.views.layout
  (:require [hiccup.page :refer [html5 include-css]]))

(defn common [& body]
  (html5
    [:head
     [:title "Welcome to pasmo-gigi-clj"]
     (include-css "/css/screen.css")]
    [:body body]))
