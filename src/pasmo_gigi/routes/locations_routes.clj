(ns pasmo-gigi.routes.locations-routes
  (:require [ring.util.response :as response]
            [ring.middleware.json :refer [wrap-json-body]]
            [compojure.core :refer [DELETE GET POST PUT context defroutes]]
            [cheshire.core :as cheshire]
            [cemerick.friend :refer [authorize current-authentication]]
            [pasmo-gigi.db.locations :as loc]
            [pasmo-gigi.db.outlet-surveys :as outlet-surveys]
            [pasmo-gigi.routes.util :refer [with-keywords json-resp]]
            [pasmo-gigi.auth-config :as auth]))

;;;;;;;;;;;;;;;;;;;; Locations ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn find-location-by-id
  [id]
  (let [location (outlet-surveys/find-surveys-for-location id)]
    (assoc json-resp :body location)))

(defn find-locations-by-type
  [location-type]
  (if (= location-type "non_traditional")
    (loc/find-all-by-type "Non-Traditional")
    (loc/find-all-by-type (clojure.string/capitalize location-type))))

(defn create-location-handler [req]
  (let [loc-map     (with-keywords (:body req))
        first-name  (:first-name (current-authentication))
        last-name   (:last-name (current-authentication))
        user-name   (str first-name " " last-name)
        created-loc (loc/create loc-map user-name)
        resp        {:body created-loc}]
    (if (nil? (:_id created-loc))
      (merge resp {:status 500})
      resp)))

(defn edit-location
  [location-id req]
  (let [loc (with-keywords (:body req))
        updated-loc (loc/update-location location-id loc)
        resp {:body updated-loc}]
    (if (nil? (:_id updated-loc))
      (merge resp {:status 500})
      resp)))

(defn find-location-by-name
  [query-params]
  (let [params (with-keywords query-params)
        location-name (:locationName params)]
    (if (empty? location-name)
      (loc/all)
      (loc/find-by-name location-name))))

(defn delete-location
  [id]
  (loc/delete id)
  {})

(defroutes routes
  (context "/api/locations" []
           (GET "/" req (authorize #{:user} (loc/all)))
           (GET "/search" {params :query-params}
                (authorize #{:user} (find-location-by-name params)))
           (GET "/:id" [id] 
                (authorize #{:user} (find-location-by-id id)))
           (DELETE "/:id" [id]
                   (authorize #{:user} (delete-location id)))
           (PUT "/:id" [id :as req]
                (authorize #{:user} (edit-location id req)))
           (POST "/" req 
                 (authorize #{:user} (create-location-handler req)))
           (GET "/byType/:location-type" [location-type]
                (authorize #{:user} (find-locations-by-type location-type)))))

