(ns restaurant-inspections-api.tasks
  (:require [clj-time.core :as t]
            [clj-time.format :as f]
            [clojure.set :as set]
            [clojure.tools.logging :as log]
            [yesql.core :refer [defqueries]]
            [clj-time.periodic :refer [periodic-seq]]
            [clojure-csv.core :as csv]
            [chime :refer [chime-at]]
            [restaurant-inspections-api.environment :as env]
            [clojure.java.jdbc :as db]
            [clojure.string :as str])
  (:import (org.joda.time DateTimeZone)))

(def db-url (env/get-env-db-url))

(defqueries "sql/inspections.sql" {:connection db-url})

(defn insert-data!
  "insert the data list into db"
  [inspections]
  (db/insert-multi! db-url :restaurant_inspections inspections))

(defn str-null->int
  "convert from blank string to number or nil"
  [str]
  (try (Integer. (not-empty str)) (catch Exception _)))

(defn str-csv-date->iso
  "convert from csv date format to iso date"
  [str]
  (try (not-empty (f/unparse (f/formatter "YYYY-MM-dd") (f/parse (f/formatter "MM/dd/YYYY") str)))
       (catch Exception _)))

(defn parse
  "transforms csv data into restaurants database data"
  [csv-file]
  (keep #(try {:district                          (not-empty (nth % 0))
                :county_number                      (str-null->int (nth % 1))
                :county_name                        (not-empty (nth % 2))
                :license_type_code                  (not-empty (nth % 3))
                :license_number                     (str-null->int (nth % 4))
                :business_name                      (not-empty (nth % 5))
                :location_address                   (not-empty (nth % 6))
                :location_city                      (not-empty (nth % 7))
                :location_zipcode                   (not-empty (nth % 8))
                :inspection_number                  (str-null->int (nth % 9))
                :visit_number                       (str-null->int (nth % 10))
                :inspection_class                   (not-empty (nth % 11))
                :inspection_type                    (not-empty (nth % 12))
                :inspection_disposition             (not-empty (nth % 13))
                :inspection_date                    (str-csv-date->iso (nth % 14))
                :critical_violations_before_2013    (str-null->int (nth % 15))
                :noncritical_violations_before_2013 (str-null->int (nth % 16))
                :total_violations                   (str-null->int (nth % 17))
                :high_priority_violations           (str-null->int (nth % 18))
                :intermediate_violations            (str-null->int (nth % 19))
                :basic_violations                   (str-null->int (nth % 20))
                :pda_status                         (= (nth % 21) "Y")
                :violation_01                       (str-null->int (nth % 22))
                :violation_02                       (str-null->int (nth % 23))
                :violation_03                       (str-null->int (nth % 24))
                :violation_04                       (str-null->int (nth % 25))
                :violation_05                       (str-null->int (nth % 26))
                :violation_06                       (str-null->int (nth % 27))
                :violation_07                       (str-null->int (nth % 28))
                :violation_08                       (str-null->int (nth % 29))
                :violation_09                       (str-null->int (nth % 30))
                :violation_10                       (str-null->int (nth % 31))
                :violation_11                       (str-null->int (nth % 32))
                :violation_12                       (str-null->int (nth % 33))
                :violation_13                       (str-null->int (nth % 34))
                :violation_14                       (str-null->int (nth % 35))
                :violation_15                       (str-null->int (nth % 36))
                :violation_16                       (str-null->int (nth % 37))
                :violation_17                       (str-null->int (nth % 38))
                :violation_18                       (str-null->int (nth % 39))
                :violation_19                       (str-null->int (nth % 40))
                :violation_20                       (str-null->int (nth % 41))
                :violation_21                       (str-null->int (nth % 42))
                :violation_22                       (str-null->int (nth % 43))
                :violation_23                       (str-null->int (nth % 44))
                :violation_24                       (str-null->int (nth % 45))
                :violation_25                       (str-null->int (nth % 46))
                :violation_26                       (str-null->int (nth % 47))
                :violation_27                       (str-null->int (nth % 48))
                :violation_28                       (str-null->int (nth % 49))
                :violation_29                       (str-null->int (nth % 50))
                :violation_30                       (str-null->int (nth % 51))
                :violation_31                       (str-null->int (nth % 52))
                :violation_32                       (str-null->int (nth % 53))
                :violation_33                       (str-null->int (nth % 54))
                :violation_34                       (str-null->int (nth % 55))
                :violation_35                       (str-null->int (nth % 56))
                :violation_36                       (str-null->int (nth % 57))
                :violation_37                       (str-null->int (nth % 58))
                :violation_38                       (str-null->int (nth % 59))
                :violation_39                       (str-null->int (nth % 60))
                :violation_40                       (str-null->int (nth % 61))
                :violation_41                       (str-null->int (nth % 62))
                :violation_42                       (str-null->int (nth % 63))
                :violation_43                       (str-null->int (nth % 64))
                :violation_44                       (str-null->int (nth % 65))
                :violation_45                       (str-null->int (nth % 66))
                :violation_46                       (str-null->int (nth % 67))
                :violation_47                       (str-null->int (nth % 68))
                :violation_48                       (str-null->int (nth % 69))
                :violation_49                       (str-null->int (nth % 70))
                :violation_50                       (str-null->int (nth % 71))
                :violation_51                       (str-null->int (nth % 72))
                :violation_52                       (str-null->int (nth % 73))
                :violation_53                       (str-null->int (nth % 74))
                :violation_54                       (str-null->int (nth % 75))
                :violation_55                       (str-null->int (nth % 76))
                :violation_56                       (str-null->int (nth % 77))
                :violation_57                       (str-null->int (nth % 78))
                :violation_58                       (str-null->int (nth % 79))
                :license_id                         (nth % 80)
                :inspection_visit_id                (str-null->int (nth % 81))}
              (catch Exception _ (log/info "Fail to load row " %)))
       (csv/parse-csv csv-file)))

(defn download
  "download csv files"
  [csv-files]
  (log/info "Downloading" (count csv-files) "CSV files...")
  (map (fn [file]
         (log/info "Downloading file " file)
         (parse (slurp file)))
       csv-files))

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