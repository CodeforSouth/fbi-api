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

(defn query-inspection-details
  [query]
  (inspection-details query))

(defn home
  "go to project wiki"
  []
  (res/redirect "https://github.com/Code-for-Miami/restaurant-inspections-api/wiki"))

(defn get-violation-count
  "dynamically get violation in an inspection row using the violation #"
  [data id]
  ((keyword (format "violation_%1$02d" id))                 ;; violation_01 .. violation_58
    data))

(defn parse-violations
  "get an inspection row and generates a list of not empty violations"
  [data]
  (keep #(let [count (get-violation-count data %)]
           (when (and (not (nil? count)) (pos? count))
             {:id % :count count}))
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
                 (inspections-by-zips
                   {:startDate    start-date
                    :endDate      end-date
                    :zips         zips})))))

(defn get-name
  "return inspections per given business name, location and period"
  ([start-date end-date name]
   (res/ok (map format-data
                (inspections-by-business
                  {:startDate    start-date
                   :endDate      end-date
                   :businessName (str/replace name #"\*" "%")}))))
  ([start-date end-date name zips]
   (let [zips (str/split zips #",")]
     (res/ok (map format-data
                  (inspections-by-business
                    {:startDate    start-date
                     :endDate      end-date
                     :businessName (str/replace name #"\*" "%")
                     :zips         zips}))))))

(defn district
  "return inspections per given district and period"
  [district start-date end-date]
  (res/ok (map format-data
               (inspections-by-district
                 {:startDate start-date
                  :endDate   end-date
                  :district  district}))))

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
  (res/ok (format-data (first (query-inspection-details {:id id})) true)))

(defn get-dist-counties
  "return district and counties list"
  []
  (res/ok (district-counties-summary)))