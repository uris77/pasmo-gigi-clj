(ns pasmo-gigi.api-handlers
  (:require [ring.util.response :as response]
            [ring.middleware.json :refer [wrap-json-body]]
            [compojure.core :refer [DELETE GET POST context defroutes routes]]
            [cemerick.friend :refer [authorize current-authentication]]
            [pasmo-gigi.db.locations :as loc]
            [pasmo-gigi.db.outlet-surveys :as outlet-surveys]
            [pasmo-gigi.db.surveys :as surveys]
            [pasmo-gigi.db.users :as users]
            [pasmo-gigi.routes.locations-routes :as locations-routes]
            [pasmo-gigi.routes.util :refer [with-keywords json-resp gen]]
            [pasmo-gigi.auth-config :as auth]))


;;;;;;;;;;;;;;;; Surveys ;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn list-surveys
  [req]
  (surveys/all))

(defn create-survey
  [req]
  (let [survey-req (with-keywords (:body req))
        first-name (:first-name (current-authentication))
        last-name (:last-name (current-authentication))
        user-name (str first-name " " last-name)]
    (surveys/create survey-req user-name)))

(defn get-survey
  [survey-id]
  (let [locs (outlet-surveys/details-for-survey survey-id)
        survey (surveys/find-by-id survey-id)]
    (assoc json-resp :body {:survey survey :locations locs})))

;;;;;;;;;;;;;;;;;;;; Locations ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn find-all-outlet-surveys
  [survey-id outlet-type]
  (outlet-surveys/find-all-by-survey-and-type survey-id outlet-type))


(defn users-handler
  [req]
  (users/all))

(defn locations-list-handler [req]
  {:headers {"Content-Type" "application/json"}
   :body (gen [])})

(defroutes basic-routes
  (context "/api" [] 
           (GET "/surveys" req
                (authorize #{:user} list-surveys ))
           (POST "/surveys" req
                 (authorize #{:user} create-survey))
           (GET "/surveys/:id" [id]
                (authorize #{:user} (get-survey id)))
           (GET "/outletSurveys/:id/:outlet-type" [id outlet-type]
                (authorize #{:user} (find-all-outlet-surveys id outlet-type)))
           (GET "/users" req users-handler)))

(def api-routes (routes basic-routes locations-routes/routes))

