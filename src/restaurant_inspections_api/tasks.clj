(ns restaurant-inspections-api.tasks
  (:require [clj-time.core :as time]
            [clojure.set :as set]

            [restaurant-inspections-api.environment :as env]
            [clojure.java.jdbc :as db])
  (:import (org.joda.time DateTimeZone)))





;(global-set-key (kbd "C-<return>") 'cider-eval-sexp-at-point)

;; (let [csv-files (env/get-csv-files)]
  ;do one by one! don't load all in memory at once. Also... lazy seq!
  ;; )




;; (defn load-n-map-csv-files
;;   []
;;   (let [csv-files (env/get-csv-files)]
;;     (download csv-files)))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;; (defn insert-data!
;;   "insert the data list into db"
;;   [inspections]
;;   (db/insert-multi! db-url :restaurant_inspections inspections))





(comment
(defn filter-new-inspections
  "filter the new inspections based in our current DB"
  [inspections]
  (let [inspections (flatten inspections)
        inspection-ids (map :inspection_visit_id inspections)
        inspections-qty (count inspection-ids)]
    (log/info "Loading " inspections-qty " inspections")
    (->> (partition-all 1000 inspection-ids)
         (map #(select-existent-ids {:ids %}))
         (flatten)
         (map :inspection_visit_id)
         (set)
         (set/difference (set inspection-ids))
         (keep #(first (filter (fn [x] (= % (:inspection_visit_id x))) inspections))))))
)

(comment
(defn process-load-data
  "load and read the CSV, compare and insert the new ones on DB"
  []
  (let [csv-files (env/get-csv-files)
        inspections (filter-new-inspections (download csv-files))]
    (log/info "new inspections to load: " (count inspections))
    (mapv (fn [rows]
            (log/info "Saved Rows: " (count (insert-data! rows))))
          (partition-all 1000 inspections))))

(defn load-api-data
  "schedules the load process"
  []
  (log/info "Scheduling Load API Data")
  (chime-at (->> (periodic-seq (.. (t/now)
                  (withZone (DateTimeZone/forID "America/New_York"))
                  (withTime 4 0 0 0))                       ; Scheduled to run every day at 4 am
                  (-> 1 t/days)))
            (fn [time]
              (log/info "Starting load data task" time)
              (process-load-data))))
)
