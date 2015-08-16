(ns pasmo-gigi.routes.util
  (:require [cheshire.core :as cheshire]))

(defn with-keywords [m]
  (into {}
        (for [[k v] m]
          [(keyword k) (if (map? v) (with-keywords v) v)])))

(def json-resp {:headers {"Content-Type" "application/json"}
                :body []})

(defn gen [o]
  (cheshire.core/generate-string o {:pretty true}))
