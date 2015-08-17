(ns pasmo-gigi.routes.outlet-surveys-routes
  (:require [compojure.core :refer [GET POST PUT context defroutes]]
            [cemerick.friend :refer [authorize current-authentication]]
            [pasmo-gigi.db.outlet-surveys :as outlet-surveys]
            [pasmo-gigi.routes.util :refer [with-keywords json-resp]]))

(defn find-all-outlet-surveys
  [survey-id outlet-type]
  (outlet-surveys/find-all-by-survey-and-type survey-id outlet-type))

(defn find-by-id
  [survey-id]
  (let [survey (outlet-surveys/find-by-id survey-id)]
    (assoc json-resp :body survey)))

(defn create-survey
  [survey-id req]
  (let [survey-map     (with-keywords (:body req))
        params         (assoc survey-map :survey-id survey-id)
        first-name     (:first-name (current-authentication))
        last-name      (:last-name (current-authentication))
        user-name      (str first-name " " last-name)
        created-survey (outlet-surveys/create-survey params user-name)
        resp           {:body created-survey}]
    (if (nil? (:_id created-survey))
      (merge resp {:status 500})
      resp)))

(defn edit-survey
  [survey-id req]
  (let [survey-map     (with-keywords (:body req))
        first-name     (:first-name (current-authentication))
        last-name      (:last-name (current-authentication))
        user-name      (str first-name " " last-name)
        updated-survey (outlet-surveys/edit-survey survey-id survey-map user-name)
        resp           {:body updated-survey}]
    (if (nil? (:_id updated-survey))
      (merge resp {:status 500})
      resp)))

(defroutes routes
  (context "/api/outletSurveys" []
           (GET "/:id/:outlet-type" [id outlet-type]
                (authorize #{:user} (find-all-outlet-surveys id outlet-type)))
           (POST "/:survey-id" [survey-id :as req]
                 (authorize #{:user} (create-survey survey-id req)))
           (GET "/:survey-id" [survey-id]
                (authorize #{:user} (find-by-id survey-id)))
           (PUT "/:survey-id" [survey-id :as req]
                (authorize #{:user} (edit-survey survey-id req)))))


