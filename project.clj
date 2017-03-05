(defproject fbi-api "0.3.0"
  :description "State of Florida Restaurants Inspections API"
  :url "https://github.com/Code-for-Miami/fbi-api"
  :license {:name "MIT License"
            :url "https://opensource.org/licenses/MIT"}
  :dependencies [[org.clojure/clojure "1.8.0"]         ;; clojure
                 [javax.servlet/servlet-api "2.5"]     ;; for server with http-kit
                 [http-kit "2.2.0"]                    ;; web server
                 [ring/ring-devel "1.5.0"]             ;; for server to be compatible with ring spec
                 [environ "1.1.0"]                     ;; to read environmental vars, including ones on profiles.clj
                 [compojure "1.5.1"]                   ;; routing library
                 [jarohen/chime "0.2.0" :exclusions [org.clojure/core.memoize
                                                     org.clojure/tools.reader]] ;; handle cron jobs
                 [org.clojure/data.csv "0.1.3"]        ;; read and write csv files
                 [org.clojure/data.json "0.2.6"]       ;; parse json. used in tests for now
                 [com.taoensso/timbre "4.8.0" :exclusions [org.clojure/tools.reader]] ;; logging library
                 [org.clojure/tools.nrepl "0.2.12"]    ;; start narepl server, to connect repl client to running app instance
                 [prismatic/schema "1.1.3"]            ;; generate docs, validate data schema
                 [yesql "0.5.3"]                       ;; SQL -> clojure
                 [metosin/compojure-api "1.1.10" :exclusions [ring/ring-core
                                                              compojure
                                                              commons-codec
                                                              clj-time
                                                              org.clojure/tools.reader]] ;; Generates /api-docs api documentation
                 [org.clojure/java.jdbc "0.6.2-alpha1"] ;; To connecto to database with java
                 [mysql/mysql-connector-java "5.1.6"]  ;; mysql jdbc adapter
                 [liberator "0.13"]]                   ;; RESTful resources with for APIs, with LOTS of defaults
  :min-lein-version "2.0.0"
  :uberjar-name "fbi-api.jar"
  :main ^:skip-aot fbi-api.core     ;; skip ahead of time compilation on REPL
  :test-selectors {:default #(not (:integration %))
                   :integration :integration
                   :unit :default
                   :all (constantly true)}
  :profiles {:uberjar {:aot :all}                      ;; ahead of time compilation for release
             :dev {:plugins [[lein-kibit "0.1.2"]
                             [lein-cljfmt "0.5.6" :exclusions [org.clojure/clojure]]
                             [quickie "0.4.2"]
                             [lein-cloverage "1.0.7" :exclusions [org.clojure/clojure]]]}})
