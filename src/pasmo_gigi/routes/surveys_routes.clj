(ns pasmo-gigi.routes.surveys-routes
  (:require [compojure.core :refer [DELETE GET POST PUT context defroutes]]
            [cemerick.friend :refer [authorize current-authentication]]
            [pasmo-gigi.db.surveys :as surveys]
            [pasmo-gigi.db.outlet-surveys :as outlet-surveys]
            [pasmo-gigi.routes.util :refer [with-keywords json-resp]]))

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

(defroutes routes
  (context "/api/surveys" []
           (GET "/" req
                (authorize #{:user} (surveys/all)))
           (POST "/" req
                 (authorize #{:user} (create-survey req)))
           (GET "/:id" [id]
                (authorize #{:user} (get-survey id)))))


