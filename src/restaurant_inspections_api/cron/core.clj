(ns restaurant-inspections-api.cron.core
  (:require [chime :refer [chime-at]]
            [clj-time.periodic :refer [periodic-seq]]
            [clojure.tools.logging :as log]
            [clj-time.core :as timer]
            ; internal
            [restaurant-inspections-api.environment :as env]
            [restaurant-inspections-api.cron.csv-to-model :as model])
  (:import (org.joda.time DateTimeZone)))


(defn process-load-data!
  "Load and read the CSV urls, and insert new records into the DB"
  []
  (let [csv-urls (env/get-csv-files)]
        (time (model/download! csv-urls))))


(defn load-api-data
  "Schedules the load process"
  []
  (log/info "Scheduling Load API Data")
  (chime-at (->> (periodic-seq (.. (timer/now)
                                   (withZone (DateTimeZone/forID "America/New_York"))
                                   (withTime 4 0 0 0)) ; Scheduled to run every day at 4 am
                               (-> 1 timer/days)))
            (fn [time]
              (log/info "Starting load data task" time)
              (process-load-data!))))
