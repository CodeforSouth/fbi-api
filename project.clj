(defproject restaurant-inspections-api "0.1.0-SNAPSHOT"
  :description "State of Florida Restaurants Inspections API"
  :url "https://github.com/Code-for-Miami/restaurant-inspections-api"
  :license {:name "MIT License"
            :url "https://opensource.org/licenses/MIT"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [javax.servlet/servlet-api "2.5"]
                 [http-kit "2.2.0-beta1"]
                 [ring/ring-devel "1.5.0"]
                 [cheshire "5.6.3"]
                 [environ "1.1.0"]
                 [compojure "1.5.1"]
                 [jarohen/chime "0.1.9"]
                 [clojure-csv/clojure-csv "2.0.1"]
                 [org.clojure/tools.logging "0.3.1"]
                 [yesql "0.5.3"]
                 [org.clojure/java.jdbc "0.6.2-alpha1"]
                 [mysql/mysql-connector-java "5.1.6"]]
  :min-lein-version "2.0.0"
  :plugins [[lein-environ "1.1.0"]]
  :uberjar-name "restaurant-inspections-api.jar"
  :main ^:skip-aot restaurant-inspections-api.core
  :profiles {:uberjar {:aot :all}})
