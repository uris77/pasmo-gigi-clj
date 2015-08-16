(ns pasmo-gigi.handler
  (:require [compojure.core :refer [defroutes routes GET]]
            [ring.middleware.resource :refer [wrap-resource]]
            [ring.middleware.file-info :refer [wrap-file-info]]
            [ring.middleware.defaults :refer [site-defaults wrap-defaults]]
            [ring.middleware.json :refer [wrap-json-response wrap-json-body]]
            [ring.util.response :as resp]
            [hiccup.middleware :refer [wrap-base-url]]
            [hiccup.page :as h]
            [selmer.parser :refer [render-file]]
            [compojure.handler :as handler]
            [compojure.route :as route]
            [cheshire.core :refer :all]
            [cemerick.friend :as friend]
            [friend-oauth2.workflow :as oauth2]
            (cemerick.friend [workflows :as workflows]
                             [credentials :as creds])
            [clj-http.client :as http-client]
            [environ.core :refer [env]]
            [pasmo-gigi.auth-config :refer [client-config uri-config credential-fn]]
            [pasmo-gigi.routes.home :refer [home-routes]]
            [pasmo-gigi.site-handlers :as site-handlers]
            [pasmo-gigi.api-handlers :as api-handlers]
            [pasmo-gigi.db.core :as db]))

(defn init []
  (cheshire.generate/add-encoder org.bson.types.ObjectId cheshire.generate/encode-str)
  (db/mongo-connection!)
  (println "pasmo-gigi-clj is starting"))


(defn index-handler [req]
  (render-file "templates/index.html" {:dev {env :dev?}}))


(defroutes public-routes
  (GET "/" req index-handler)
)

(defn destroy []
  (db/disconnect!)
  (println "pasmo-gigi-clj is shutting down"))

(def api-app
  (-> api-handlers/api-routes
      wrap-json-body
      wrap-json-response))

(def site-app
  (-> (routes api-app site-handlers/site-routes)
      (friend/authenticate {:allow-annon? true
                            :login-uri "/login"
                            :default-landing-uri "/app"
                            :unauthorized-handler #(-> (h/html5 [:h2 "You do not have sufficient privileges to access " (:uri %)])
                                        resp/response
                                        (resp/status 401))
                            :workflows [(oauth2/workflow
                                         {:client-config client-config
                                          :uri-config    uri-config
                                          :credential-fn credential-fn})]})
))

(def site-and-api
  (wrap-defaults (routes public-routes site-app) (assoc-in site-defaults [:security :anti-forgery] false)))

