(defproject pasmo-gigi "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [environ "1.0.0"]
                 [compojure "1.4.0"]
                 [hiccup "1.0.5"]
                 [ring-server "0.4.0"]
                 [com.cemerick/friend "0.2.1" :exclusions [ring/ring-core org.clojure/core.cache org.apache.httpcomponents/httpclient]]
                 [friend-oauth2 "0.1.3" :exclusions [commons-logging org.apache.httpcomponents/httpcore]]]
  :plugins [[lein-ring "0.9.6"]
            [lein-environ "1.0.0"]]
  :ring {:handler pasmo-gigi.handler/app
         :init    pasmo-gigi.handler/init
         :destroy pasmo-gigi.handler/destroy}
  :profiles
  {:uberjar    {:aot :all}

   :dev-common {:dependencies [[ring/ring-mock "0.2.0"] [ring/ring-devel "1.4.0"]]}
   :dev        [:dev-common]
   :production {:ring
                {:open-browser? false, :stacktraces? false, :auto-reload? false}}
   :production [:production-common]})
