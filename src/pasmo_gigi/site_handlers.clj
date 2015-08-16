(ns pasmo-gigi.site-handlers
  (:require [ring.util.response :as response]
            [compojure.core :refer [GET POST ANY defroutes]]
            [compojure.route :refer [not-found resources]]
            [environ.core :refer [env]]
            [cemerick.friend :as friend]
            (cemerick.friend [credentials :as creds])
            [selmer.parser :refer [render-file]]
            [pasmo-gigi.auth-config :as auth]))

(defn main-handler []
  (render-file "templates/main.html" {:dev {env :dev?}}))

(defroutes site-routes
  (GET "/login" req main-handler)
  (GET "/hello" req
       "HELLO" (friend/authorize #{:pasmo-gigi.auth-config/user} main-handler))
  (GET "/app" req
       (friend/authorize #{:user} (main-handler)))
  (GET "/oauth2callback" req
       (friend/authorize #{:pasmo-gigi.auth-config/user} main-handler))
  (friend/logout (ANY "/logout" request (response/redirect "/")))
  (resources "/")
  (not-found "Not Found"))

