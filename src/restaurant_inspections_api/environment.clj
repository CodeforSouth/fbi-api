(ns restaurant-inspections-api.environment
  (:require [environ.core :refer [env]]
            [clojure.tools.logging :as log]
            [clojure.string :as str]))

(defn get-env-port
  "detect PORT environment variable"
  []
  (if-let [port (env :port)]
    (do (log/info "Environment variable PORT detected: " port)
        (Integer. port))
    (do (log/info "No-Environment variable PORT, setting default port as 8080")
        8080)))

(defn get-env-db-url
  "detect DATABASE_URL environment variable"
  []
  (if-let [url (or (env :database-url) (env :cleardb-database-url)) ]
    (do (log/info "Environment variable DATABASE_URL detected: " url)
        url)
    (let [default-db "jdbc:mysql://localhost:3306/cfm_restaurants?user=root"]
      (log/info "No-Environment variable DATABASE_URL, setting default url as " default-db)
      default-db)))

(defn in-prod?
  "verifies if it's in production mode (environment variable PRODUCTION)"
  []
  (if-let [production (env :production)]
    (do (log/info "Production Mode ON, environment variable PRODUCTION=" production)
        true)
    (do (log/info "No-Environment variable PRODUCTION, production mode false")
        false)))

(defn get-csv-files
  "get restaurant inspections csv files address separated by comma"
  []
  (if-let [csv-files (env :csv-files)]
    (do (log/info "Restaurant inspections CSV files in environment variable CSV_FILES: "
             csv-files)
        (str/split csv-files #","))
    (let [default-csv-files ["ftp://dbprftp.state.fl.us/pub/llweb/1fdinspi.csv"
                             "ftp://dbprftp.state.fl.us/pub/llweb/2fdinspi.csv"
                             "ftp://dbprftp.state.fl.us/pub/llweb/3fdinspi.csv"
                             "ftp://dbprftp.state.fl.us/pub/llweb/4fdinspi.csv"
                             "ftp://dbprftp.state.fl.us/pub/llweb/5fdinspi.csv"
                             "ftp://dbprftp.state.fl.us/pub/llweb/6fdinspi.csv"
                             "ftp://dbprftp.state.fl.us/pub/llweb/7fdinspi.csv"]]
      (log/info "No-Environment variable CSV_FILES, setting default csv files as "
           default-csv-files)
      default-csv-files)))