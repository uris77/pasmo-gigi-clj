(ns pasmo-gigi.server
  (:use org.httpkit.server)
  (:require [pasmo-gigi.handler :as h])
  (:gen-class))

(defn -main [& args]
  (let [port (Integer/parseInt (or (System/getenv "PORT") "3449"))]
    (h/init)
    (run-server h/site-and-api {:port port :join? false})))
