(ns restaurant-inspections-api.cron.core
  (:require [chime :refer [chime-at]]
            [clj-time.periodic :refer [periodic-seq]]
            [clojure.tools.logging :as log]
            [clj-time.core :as timer]
            ; internal
            [restaurant-inspections-api.environment :as env]
            [restaurant-inspections-api.cron.csv-to-model :as model])
  (:import (org.joda.time DateTimeZone)))

(def chime-time (env/get-env-chime))

(defn process-load-data!
  "Load and read the CSV urls, and insert new records into the DB"
  []
  (let [csv-urls (env/get-csv-files)]
        (time (model/download! csv-urls))))

(defn load-api-data
  "schedules the load process"
  []
  (let [[hour min sec mili] chime-time]
    (log/info (str "Scheduling Load API Data to run at " hour ":" min ":" sec "." mili))
    (chime-at (->> (periodic-seq (.. (t/now)
                                     (withZone (DateTimeZone/forID "America/New_York"))
                                     (withTime hour min sec mili))           ; Scheduled to run every day at CHIME-TIME
                                 (-> 1 t/days)))
              (fn [time]
                (log/info "Starting load data task" time)
                (process-load-data!)))))
