(ns pasmo-gigi.db.locations
  (:import org.bson.types.ObjectId
           java.util.Date)
  (:require [environ.core :refer [env]]
            [monger.core :as mg]
            [monger.collection :as coll]
            [pasmo-gigi.db.core :as db :refer [connect! mongo-connection!]]))

(def mconn (mongo-connection!))
(def db (:db mconn))
(def conn (:conn mconn))
(def locations-coll "pasmo_locations")

(defn all
  "List all locations"
  []
  (->> (coll/find-maps db locations-coll)
       (map #(assoc % :id (:_id %)))))

(defn find-by-name
  "Find a location with the specified name"
  [location-name]
  {:pre [(string? location-name)]}
  (let [query {"$text" {"$search" location-name}}]
    (->> (coll/find-maps db locations-coll query)
        (map #(assoc % :id (:_id %))))))

(defn find-by-id
  "Find a location with the specified id."
  [location-id]
  {:pre [(string? location-id)]}
  (let [oid (ObjectId. location-id)
        location (coll/find-map-by-id db locations-coll oid)]
    (if location
      (assoc location :id (:_id location))
      {})))


(defn create
  "Create a new location record."
  [loc-map user-name]
  {:pre [(string? user-name)]}
  (let [loc     (merge loc-map {:deleted     false
                                :dateCreated (Date.)
                                :createdBy   user-name})
        lon     (Double/parseDouble (get-in loc-map [:loc "lon"]))
        lat     (Double/parseDouble (get-in loc-map [:loc "lat"]))
        new-loc (coll/insert-and-return db 
                                        locations-coll
                                        (assoc loc-map :loc 
                                               {:type "Point" 
                                                :coordinates [lon lat]}))]
    (assoc new-loc :id (:_id new-loc))))

(defn delete
  "Delete a location."
  [location-id]
  {:pre [(string? location-id)]}
  (let [oid (ObjectId. location-id)]
    (coll/remove-by-id db locations-coll oid)))

(defn update-location
  "Update an existing location."
  [location-id update-params]
  {:pre [(string? location-id)]}
  (let [oid (ObjectId. location-id)
        lon (Double/parseDouble (get-in update-params [:loc :lon]))
        lat (Double/parseDouble (get-in update-params [:loc :lat]))
        update-map (-> update-params 
                       (assoc :loc {:type "Point" :coordinates [lon lat]})
                       (dissoc :_id))]
    (coll/update-by-id db locations-coll oid update-map)
    (find-by-id location-id)))


(defn count-by-type
  "Counts the total number of locations for a certain type of location."
  [location-type]
  {:pre [(string? location-type)]}
  (coll/count db locations-coll {:locationType location-type}))

(defn find-all-by-type
  [location-type]
  {:pre [(string? location-type)]}
  (as-> (coll/find-maps db locations-coll {:locationType location-type}) $
    (map #(assoc % :id (:_id %)) $)))

