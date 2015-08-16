(ns pasmo-gigi.db.core
  (:require [environ.core :refer [env]]
            [monger.core :as mg]
            [monger.collection :as coll]))

(def mongo-connection (atom {}))

(defn connect!
  []
  (let [uri (env :mongo-uri)
        {:keys [conn db]} (mg/connect-via-uri uri)]
    {:conn conn :db db}))


(defn disconnect!
  []
  (mg/disconnect (:conn @mongo-connection)))

(defn mongo-connection!
  []
  (if (empty? @mongo-connection)
    (do 
      (reset! mongo-connection (connect!))
      @mongo-connection)
    @mongo-connection))

