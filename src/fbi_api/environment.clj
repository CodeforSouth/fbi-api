(ns fbi-api.environment
  (:require [environ.core :refer [env]]
            [taoensso.timbre :refer [info]]))

(defn get-env-port
  "Detect PORT environment variable."
  []
  (if-let [port (env :port)]
    (do (info "Environment variable PORT detected: " port)
        (Integer. port))
    (do (info "No-Environment variable PORT, setting default port as 8080")
        8080)))

(defn get-env-chime
  "Detect chime environment variable - tasks time."
  []
  (if (env :chime-time)
    (let [chime-time (clojure.string/split (or (env :chime-time) "") #",")]
      (info "Environment variable CHIME_TIME detected: " chime-time)
      (map #(Integer. %) chime-time))
    (do (info "No-Environment variable CHIME_TIME, setting default time to 4 am")
        [4 0 0 0])))

(defn production?
  "Verifies if it's in production mode (environment variable PRODUCTION)."
  []
  (if-let [production (env :production)]
    (do (info "Production Mode ON, environment variable PRODUCTION=" production)
        true)
    (do (info "No-Environment variable PRODUCTION, production mode false")
        false)))

(defn get-env-db-url
  "Detect DATABASE_URL environment variable."
  []
  (if-let [url (or (env :database-url) (env :cleardb-database-url))]
    (do (when-not (production?) (prn  "Environment variable DATABASE_URL detected: " url))
        url)
    (let [default-db "jdbc:mysql://localhost:3306/cfm_restaurants?user=root"]
      (info "No-Environment variable DATABASE_URL, setting default url as " default-db)
      default-db)))

(defn get-csv-files
  "Get restaurant inspections csv files address separated by comma."
  []
  (if-let [csv-files (env :csv-files)]
    (do (info "Restaurant inspections CSV files in environment variable CSV_FILES: "
              csv-files)
        (clojure.string/split csv-files #","))
    (let [default-csv-files ["ftp://dbprftp.state.fl.us/pub/llweb/1fdinspi.csv"
                             "ftp://dbprftp.state.fl.us/pub/llweb/2fdinspi.csv"
                             "ftp://dbprftp.state.fl.us/pub/llweb/3fdinspi.csv"
                             "ftp://dbprftp.state.fl.us/pub/llweb/4fdinspi.csv"
                             "ftp://dbprftp.state.fl.us/pub/llweb/5fdinspi.csv"
                             "ftp://dbprftp.state.fl.us/pub/llweb/6fdinspi.csv"
                             "ftp://dbprftp.state.fl.us/pub/llweb/7fdinspi.csv"]]
      (info "No-Environment variable CSV_FILES, setting default csv files as "
            default-csv-files)
      default-csv-files)))
