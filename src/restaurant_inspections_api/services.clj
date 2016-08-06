(ns restaurant-inspections-api.services
  (:require [restaurant-inspections-api.responses :as res]
            [clj-time.format :as f]
            [clj-time.coerce :as c]
            [yesql.core :refer [defqueries]]
            [restaurant-inspections-api.environment :as env]
            [clojure.java.jdbc :as db]
            [clojure.string :as str]))

(def db-url (env/get-env-db-url))

(defqueries "sql/inspections.sql" {:connection db-url})

(defn home
  "go to project wiki"
  []
  (res/redirect "https://github.com/Code-for-Miami/restaurant-inspections-api/wiki"))

(defn basic-fields-sql
  "return a string with the basic inspection fields"
  []
  (str "inspection_visit_id, district, county_number, county_name, license_type_code, license_number, "
       "business_name, inspection_date, location_address, location_city, location_zipcode, "
       "inspection_number, visit_number, inspection_type, inspection_disposition, "
       "total_violations, high_priority_violations, "
       "intermediate_violations, basic_violations"))

(defn get-violation-count
  "dynamically get violation in an inspection row using the violation #"
  [data id]
  ((keyword (format "violation_%1$02d" id))                 ;; violation_01 .. violation_58
    data))

(defn parse-violations
  "get an inspection row and generates a list of not empty violations"
  [data]
  (keep #(let [count (get-violation-count data %)]
           (when (pos? count) {:id % :count count}))
       (range 1 59)))

(defn format-data
  "formats db raw data to json pattern"
  ([data]
   (format-data data false))
  ([data is-full]
    (let [basic-data {:id                              (:inspection_visit_id data)
                      :district                        (:district data)
                      :countyNumber                    (:county_number data)
                      :countyName                      (:county_name data)
                      :licenseTypeCode                 (:license_type_code data)
                      :licenseNumber                   (:license_number data)
                      :businessName                    (:business_name data)
                      :inspectionDate                  (f/unparse (f/formatter "YYYY-MM-dd")
                                                                  (c/from-date (:inspection_date data)))
                      :locationAddress                 (:location_address data)
                      :locationCity                    (:location_city data)
                      :locationZipcode                 (:location_zipcode data)
                      :inspectionNumber                (:inspection_number data)
                      :visitNumber                     (:visit_number data)
                      :inspectionType                  (:inspection_type data)
                      :inspectionDisposition           (:inspection_disposition data)
                      :totalViolations                 (:total_violations data)
                      :highPriorityViolations          (:high_priority_violations data)
                      :intermediateViolations          (:intermediate_violations data)
                      :basicViolations                 (:basic_violations data)}]
      (if is-full
        (assoc basic-data
               :criticalViolationsBefore2013      (:critical_violations_before_2013 data)
               :nonCriticalViolationsBefore2013   (:noncritical_violations_before_2013 data)
               :pdaStatus                         (:pda_status data)
               :licenseId                         (:license_id data)
               :violations (parse-violations data))
        basic-data))))

(defn location
  "return inspections per given location and period"
  [start-date end-date zips]
  (let [zips (str/split zips #",")]
    (res/ok (map format-data
                 (db/query db-url
                           (concat [(str "SELECT " (basic-fields-sql)
                                       "  FROM restaurant_inspections"
                                       " WHERE inspection_date BETWEEN ? AND ?"
                                       "   AND location_zipcode IN ("
                                       (str/join ", " (take (count zips) (repeat "?")))
                                       ") LIMIT 1000") start-date end-date] zips))))))

(def name-basic-sql
  (str "SELECT " (basic-fields-sql)
       "  FROM restaurant_inspections"
       " WHERE inspection_date BETWEEN ? AND ?"
       "   AND business_name LIKE ? "))

(defn get-name
  "return inspections per given business name, location and period"
  ([start-date end-date name]
   (res/ok (map format-data
                (db/query db-url
                          [(str name-basic-sql
                                " LIMIT 1000") start-date end-date
                           (str/replace name #"\*" "%")]))))
  ([start-date end-date name zips]
   (let [zips (str/split zips #",")]
     (res/ok (map format-data
                  (db/query db-url
                            (concat [(str name-basic-sql
                                          " AND location_zipcode IN ("
                                          (str/join ", " (take (count zips) (repeat "?")))
                                          ") LIMIT 1000") start-date end-date
                                     (str/replace name #"\*" "%")] zips)))))))

(defn district
  "return inspections per given district and period"
  [district start-date end-date]
  (res/ok (map format-data
               (inspections-by-district
                 {:startDate    start-date
                  :endDate      end-date
                  :countyNumber district}))))

(defn county
  "return inspections per given county and period"
  [countyNumber start-date end-date]
  (res/ok (map format-data
               (inspections-by-county {:startDate    start-date
                                       :endDate      end-date
                                       :countyNumber countyNumber}))))

(defn get-details
  "return full info for the given Id"
  [id]
  (res/ok (format-data (first (inspection-details {:id id})) true)))

(defn get-dist-counties
  "return district and counties list"
  []
  (res/ok district-counties-summary))