(ns fbi-api.cron.csv-to-model
  (:require [clojure.data.csv :as csv]
            [clojure.java.io :as io]
            [clojure.string :as str]
            [taoensso.timbre :refer [info]]
            ;; internal
            [fbi-api.util :refer [str-null->int str-csv-date->iso todays-date]]
            [fbi-api.db :as db]))

(declare csv-row->map)

(defn violations
  "Returns a vector sequence from a csv map, with violation > 0."
  [csv-map]
  (filter #(and (re-find #"violation_" (name (first %))) (pos? (second %))) csv-map))

(defn create-models-rows!
  "Receives a csv-map and calls database to insert *new* inspections and restaurant data, if needed."
  [csv-map]

  ;; TODO: return rows affected, format:
  ;; (defquery insert-person<! "sql/insert-person.sql")
  ;; (insert-person<! db-spec "Emma" "Thompson" "emma@thompson.com")

  (db/insert-county! csv-map)
  (db/insert-restaurant! csv-map)
  (let [modified-inspections (db/insert-inspection! csv-map)
        violations-seq (violations csv-map)]
    ; TODO: is this when-not doing what it's supposed to do?
    (when-not (zero? modified-inspections)
      (doseq [entry violations-seq]
        (db/insert-inspection-violation!
         {:inspection_id (:inspection_visit_id csv-map)
          :violation_id (Integer. (re-find #"\d+" (name (first entry))))
          :violation_count (second entry)})))))

(defn csv-seq->db!
  "Parses a sequence of csv files, one row at a time."
  [csv-seq]
  (dorun (pmap #(create-models-rows! (csv-row->map %)) csv-seq)))

(defn csv-url->db!
  "Receives one csv url and parses into, inserting new values into db."
  [csv-url]
  (with-open [in-file (io/reader csv-url)]
    (csv-seq->db! (csv/read-csv in-file))))

;; TODO: modify download! to return the amount of rows it modified etc?
(defn download!
  "Download CSV files from urls and store new entries in db."
  [csv-urls]
  (info "Downloading" (count csv-urls) "CSV files...")
  (pmap (fn [file]
          (info "Downloading file " file)
          (csv-url->db! file)) csv-urls))

;; Alternatives: (but wouldn't validate not-empty, str->int, etc)
;; (zipmap fields values)
;; (into {} (map vector fields values))
(defn csv-row->map
  "Transforms csv data into map with restaurant database keys."
  [csv-row]
  (#(try {:district                           (not-empty (nth % 0))
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
          :license_id                         (nth % 80)
          :inspection_visit_id                (str-null->int (nth % 81))
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
          :created_on                         (todays-date)
          :modified_on                        (todays-date)}
         (catch Exception e (do (info "Failed to load row " %)
                                (info (str "... due to error: " e))))) csv-row))
