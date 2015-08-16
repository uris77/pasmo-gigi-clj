(ns pasmo-gigi.db.outlet-surveys
    (:import org.bson.types.ObjectId)
    (:require [environ.core :refer [env]]
              [monger.core :as mg]
              [monger.collection :as coll]
              [pasmo-gigi.db.core :as db :refer [connect! mongo-connection!]]
              [pasmo-gigi.db.surveys :as surveys]
              [pasmo-gigi.db.locations :as locations]))

(def mconn (mongo-connection!))
(def db (:db mconn))
(def conn (:conn mconn))
(def osurveys-coll "outlet_surveys")
(def location-types #{"Traditional" "Non-Traditional" "Hotspot"})

(defn find-surveys-at-location
  [location-id]
  {:pre [(string? location-id)]}
  (let [oid (ObjectId. location-id)
        surveys (coll/find-maps db osurveys-coll {:location.id oid})]
    (map #(assoc % :id (:_id %)) surveys)))

(defn count-surveys-by-location-type
  "Counts the surveys held and groups them by location type."
  [survey-id location-type]
  {:pre [(string? survey-id)]}
  (let [survey (surveys/find-by-id survey-id)
        oid (ObjectId. survey-id)
        location-types #{"Traditional" "Non-Traditional" "Hotspot"}]
    (cond
      (= location-type "Traditional") (coll/count db osurveys-coll {:survey.id oid :outletType "traditional"})
      (= location-type "Non-Traditional") (coll/count db osurveys-coll {:survey.id oid :outletType "non-traditional"})
      :else (coll/count db osurveys-coll {:survey.id oid :outletType "hotspot"}))))

(defn details-for-survey
  "Retrieves the details for a survey with the given id."
  [survey-id]
  {:pre [(string? survey-id)]}
  (let [survey (surveys/find-by-id survey-id)]
    (reduce (fn [acc location-type]
              (conj acc {:name location-type 
                         :totalLocations (locations/count-by-type location-type) 
                         :surveyed (count-surveys-by-location-type survey-id location-type)}))
            []
            location-types)))


(defn find-surveys-for-location
  [location-id]
  {:pre [(string? location-id)]}
  (let [location (locations/find-by-id location-id)
        surveys (find-surveys-at-location location-id)]
    {:location location :surveys surveys}))

(defn find-all-by-survey-and-type
  "Retrieves all the outlet surveys for the survey
  with the specified survey id and of the given outlet type."
  [survey-id outlet-type]
  {:pre [(string? survey-id)]}
  (let [oid (ObjectId. survey-id)
        surveys (coll/find-maps db osurveys-coll {:outletType outlet-type :survey.id oid})]
    (map #(assoc % :id (:_id %)) surveys)))

