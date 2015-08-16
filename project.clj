(defproject pasmo-gigi "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [environ "1.0.0"]
                 [compojure "1.4.0"]
                 [hiccup "1.0.5"]
                 [cheshire "5.5.0"]
                 [clj-http "2.0.0"]
                 [ring-server "0.4.0"]
                 [ring/ring-defaults "0.1.5"]
                 [ring/ring-json "0.4.0"]
                 [com.cemerick/friend "0.2.1" :exclusions [ring/ring-core org.clojure/core.cache org.apache.httpcomponents/httpclient]]
                 [friend-oauth2 "0.1.3" :exclusions [commons-logging org.apache.httpcomponents/httpcore]]
                 [com.novemberain/monger "3.0.0"]
                 [selmer "0.8.7"]]
  :plugins [[lein-ring "0.9.6"]
            [lein-environ "1.0.0"]]
  :ring {:handler pasmo-gigi.handler/site-and-api
         :init    pasmo-gigi.handler/init
         :destroy pasmo-gigi.handler/destroy}
  :profiles
  {:uberjar           {:aot :all}

   :dev-common        {:dependencies [[ring/ring-mock "0.2.0"] [ring/ring-devel "1.4.0"]]
                       :env {:dev? true}}
   :dev               [:dev-env-vars :dev-common]
   :production-common {:ring
                       {:open-browser? false, :stacktraces? false, :auto-reload? false}}
   :production        [:production-common]})
