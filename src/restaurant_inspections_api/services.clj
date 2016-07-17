(ns restaurant-inspections-api.services
  (:require [restaurant-inspections-api.responses :as res]
            [clj-time.format :as f]
            [clj-time.coerce :as c]
            [restaurant-inspections-api.environment :as env]
            [clojure.java.jdbc :as db]
            [clojure.string :as str]))

(def db-url (env/get-env-db-url))

(defn home
  "go to project wiki"
  []
  (res/redirect "https://github.com/Code-for-Miami/restaurant-inspections-api/wiki"))

(defn basic-fields-sql
  "return a string with the basic inspection fields"
  []
  (str "id, district, county_number, county_name, license_type_code, license_number, "
       "business_name, inspection_date, location_address, location_city, location_zipcode, "
       "inspection_number, visit_number, inspection_type, inspection_disposition, "
       "inspection_visit_id, total_violations, high_priority_violations, "
       "intermediate_violations, basic_violations"))

(defn format-data
  "formats db raw data to json pattern"
  [data]
  {:id                              (:id data)
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
   :inspectionVisitId               (:inspection_visit_id data)
   :totalViolations                 (:total_violations data)
   :highPriorityViolations          (:high_priority_violations data)
   :intermediateViolations          (:intermediate_violations data)
   :basicViolations                 (:basic_violations data)})

(defn top10
  "return the top 10 worse (most violations) inspections"
  ([] (top10 "1970-01-01" "9999-12-31"))
  ([start-date end-date]
   (prn [(str "SELECT " (basic-fields-sql)
              "  FROM restaurant_inspections "
              " WHERE inspection_date BETWEEN ? AND ? "
              " ORDER BY total_violations DESC "
              " LIMIT 10") start-date end-date])
   (res/ok (map format-data
                (db/query db-url
                          [(str "SELECT " (basic-fields-sql)
                                "  FROM restaurant_inspections "
                                " WHERE inspection_date BETWEEN ? AND ? "
                                " ORDER BY total_violations DESC "
                                " LIMIT 10") start-date end-date])))))

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

(defn district
  "return inspections per given district and period"
  [id start-date end-date]
  (res/ok (map format-data
               (db/query db-url
                         [(str "SELECT " (basic-fields-sql)
                                     "  FROM restaurant_inspections"
                                     " WHERE inspection_date BETWEEN ? AND ?"
                                     "   AND district = ?"
                                     " LIMIT 1000") start-date end-date id] ))))

(defn county
  "return inspections per given county and period"
  [id start-date end-date]
  (res/ok (map format-data
               (db/query db-url
                         [(str "SELECT " (basic-fields-sql)
                                     "  FROM restaurant_inspections"
                                     " WHERE inspection_date BETWEEN ? AND ?"
                                     "   AND county_number = ?"
                                     " LIMIT 1000") start-date end-date id] ))))

(defn get-details
  "return full info for the given Id"
  [id]
  (res/ok (map format-data
               (db/query db-url
                         ["SELECT *
                             FROM restaurant_inspections
                            WHERE id = ?" id]))))