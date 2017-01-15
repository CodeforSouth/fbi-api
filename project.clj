(defproject restaurant-inspections-api "0.1.0-SNAPSHOT"
  :description "State of Florida Restaurants Inspections API"
  :url "https://github.com/Code-for-Miami/restaurant-inspections-api"
  :license {:name "MIT License"
            :url "https://opensource.org/licenses/MIT"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [javax.servlet/servlet-api "2.5"]
                 [http-kit "2.2.0-beta1"]
                 [ring/ring-devel "1.5.0"]
                 [environ "1.1.0"]
                 [compojure "1.5.1"]
                 [jarohen/chime "0.1.9"]
                 [org.clojure/data.csv "0.1.3"]
                 [com.taoensso/timbre "4.7.4"]
                 [org.clojure/tools.nrepl "0.2.12"]
                 [yesql "0.5.3"]
                 [org.clojure/java.jdbc "0.6.2-alpha1"]
                 [mysql/mysql-connector-java "5.1.6"]
                 [liberator "0.13"]]
  :min-lein-version "2.0.0"
  :uberjar-name "restaurant-inspections-api.jar"
  :main ^:skip-aot restaurant-inspections-api.core ; skip ahead of time compilation on REPL
  :profiles {:uberjar {:aot :all} ; ahead of time compilation for release
             :dev {:plugins [[lein-kibit "0.1.2"]
                             [lein-cloverage "1.0.7"]
                             [lein-cljfmt "0.5.6"]]}})
