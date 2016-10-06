(ns restaurant-inspections-api.cron.core
  (:require [chime :refer [chime-at]]
            [clj-time.periodic :refer [periodic-seq]]
            [taoensso.timbre :refer [info]]
            [clj-time.core :as timer]
                                        ; internal
            [restaurant-inspections-api.environment :as env]
            [restaurant-inspections-api.cron.csv-to-model :as model])
  (:import (org.joda.time DateTimeZone)))


(defn process-load-data!
  "Download and read the urls with restaurant inspections CSV files, inserting new records into database."
  []
  (let [csv-urls (env/get-csv-files)]
    (time (model/download! csv-urls))))


(defn load-api-data
  "Schedules cron job to update database with latest restaurant inspections."
  []
  (let [[hour min sec mili] (env/get-env-chime)]
    (info (str "Scheduling Load API Data to run at " hour ":" min ":" sec "." mili))
                                        ; Scheduled to run every day at CHIME-TIME
    (chime-at (->> (periodic-seq (.. (timer/now)
                                     (withZone (DateTimeZone/forID "America/New_York"))
                                     (withTime hour min sec mili))  
                                 (-> 1 timer/days)))
              (fn [time]
                (info "Starting load data task" time)
                (process-load-data!)))))
