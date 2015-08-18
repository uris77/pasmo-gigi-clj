(defproject pasmo-gigi "1.0.0-SNAPSHOT"
  :description "PASMO's GIGI CRUD App."
  :url "http://pasmo.bz"
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [environ "1.0.0"]
                 [compojure "1.4.0"]
                 [hiccup "1.0.5"]
                 [cheshire "5.5.0"]
                 [clj-http "2.0.0"]
                 [ring-server "0.4.0" :exclusions [org.eclipse.jetty/jetty-http 
                                                   org.eclipse.jetty/jetty-continuation]]
                 [ring/ring-defaults "0.1.5"]
                 [ring/ring-json "0.4.0"]
                 [http-kit "2.1.18"]
                 [com.cemerick/friend "0.2.1" :exclusions [ring/ring-core 
                                                           org.clojure/core.cache 
                                                           org.apache.httpcomponents/httpclient]]
                 [friend-oauth2 "0.1.3" :exclusions [commons-logging 
                                                     org.apache.httpcomponents/httpcore]]
                 [com.novemberain/monger "3.0.0"]
                 [selmer "0.8.7"]]
  :plugins [[lein-ring "0.9.6"]
            [lein-environ "1.0.0"]]

  :main pasmo-gigi.server

  :uberjar-name "pasmo-gigi.jar"

  :ring {:handler pasmo-gigi.handler/site-and-api
         :init    pasmo-gigi.handler/init
         :destroy pasmo-gigi.handler/destroy}

  :profiles {:uberjar-common    {:aot         :all
                                 :omit-source true
                                 :main pasmo-gigi.server}
             :uberjar           [:uberjar-common :uberjar-env-vars]
             :dev-common        {:dependencies [[ring/ring-mock "0.2.0"] [ring/ring-devel "1.4.0"]]
                                 :env {:dev? true}
                                 :open-browser? true}
             :dev               [:dev-env-vars :dev-common]

             :production-common {:ring {:open-browser? false 
                                        :stacktraces? false
                                        :auto-reload? false}}
             :production        [:production-common]})
