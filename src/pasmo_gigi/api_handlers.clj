(ns pasmo-gigi.api-handlers
  (:require [ring.util.response :as response]
            [ring.middleware.json :refer [wrap-json-body]]
            [compojure.core :refer [DELETE GET POST context defroutes routes]]
            [cemerick.friend :refer [authorize current-authentication]]
            [pasmo-gigi.routes.outlet-surveys-routes :as outlet-surveys-routes]
            [pasmo-gigi.routes.locations-routes :as locations-routes]
            [pasmo-gigi.routes.surveys-routes :as surveys-routes]
            [pasmo-gigi.routes.util :refer [with-keywords json-resp gen]]
            [pasmo-gigi.auth-config :as auth]))


(def api-routes (routes locations-routes/routes 
                        surveys-routes/routes 
                        outlet-surveys-routes/routes))

