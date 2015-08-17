(ns pasmo-gigi.api-handlers
  (:require [ring.util.response :as response]
            [ring.middleware.json :refer [wrap-json-body]]
            [compojure.core :refer [DELETE GET POST context defroutes routes]]
            [cemerick.friend :refer [authorize current-authentication]]
            [pasmo-gigi.db.users :as users]
            [pasmo-gigi.routes.outlet-surveys-routes :as outlet-surveys-routes]
            [pasmo-gigi.routes.locations-routes :as locations-routes]
            [pasmo-gigi.routes.surveys-routes :as surveys-routes]
            [pasmo-gigi.routes.util :refer [with-keywords json-resp gen]]
            [pasmo-gigi.auth-config :as auth]))



;;;;;;;;;;;;;;;;;;;; Locations ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn users-handler
  [req]
  (users/all))


(defroutes basic-routes
  (context "/api" [] 
           (GET "/users" req users-handler)))

(def api-routes (routes basic-routes 
                        locations-routes/routes 
                        surveys-routes/routes 
                        outlet-surveys-routes/routes))

