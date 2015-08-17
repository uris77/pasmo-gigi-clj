(ns pasmo-gigi.db.surveys
  (:import org.bson.types.ObjectId
           java.util.Date)
  (:require [monger.core :as mg]
            [monger.collection :as coll]
            [monger.query :as mq]
            [pasmo-gigi.db.core :refer [mongo-connection!]]))

(def mconn (mongo-connection!))
(def db (:db mconn))
(def conn (:conn mconn))
(def surveys-coll "pasmo_surveys")

(def months {"January"   1
             "February"  2
             "March"     3
             "April"     4
             "May"       5
             "June"      6
             "July"      7
             "August"    8
             "September" 9
             "October"   10
             "November"  11
             "December"  12})

(defn- add-month-order
  "Adds a monthOrder to the survey. For example, January will have a monthOrder of 1.
  This is for sorting purposes."
  [survey-map]
  (let [month (:month survey-map) ]
    (assoc survey-map :monthOrder (get-in months month))))

(defn create
  "Create a survey"
  [survey-map user-name]
  (let [dateCreated (Date.)
        year (Integer/parseInt (:year survey-map))
        survey (-> (assoc survey-map :dateCreated dateCreated)
                   (assoc-in [:userName] user-name)
                   (assoc-in [:year] year)
                   (assoc-in [:monthOrder] (add-month-order survey-map)))]

    (coll/insert-and-return db surveys-coll survey)))


(defn all
  "List all surveys."
  []
  (let [surveys (mq/with-collection db surveys-coll
                  (mq/find {})
                  (mq/sort (array-map :year -1 :monthOrder 1)))]
    (map #(assoc % :id (:_id %)) surveys)))

(defn find-by-id
  "Find a survey with the given id."
  [survey-id]
  {:pre [(string? survey-id)]}
  (let [oid (ObjectId. survey-id)
        survey (coll/find-map-by-id db surveys-coll oid)]
    (assoc survey :id (:_id survey))))


(defn find-by-location
  "Finds all surveys conducted at a location with the given location id."
  [location-id]
  {:pre [(string? location-id)]}
  (let [surveys (coll/find-maps db surveys-coll {:location.id location-id})]
    (map #(assoc % :id (:_id %)) surveys)))


