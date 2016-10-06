(ns restaurant-inspections-api.services
  (:require [restaurant-inspections-api.responses :as res]
            [clj-time.format :as timef]
            [clj-time.coerce :as coerce-time]
            [yesql.core :refer [defqueries]]
            [restaurant-inspections-api.db :as db]
            [clojure.string :as str]))

(defn home
  "Root: Navigate to project wiki."
  []
  (res/redirect "https://github.com/Code-for-Miami/restaurant-inspections-api/wiki"))

(defn format-data
  "Format db raw data to json."
  ([data]
   (format-data data false))
  ([data is-full]
   (let [basic-data {:id                     (:inspection_visit_id data)
                     :district               (:district data)
                     :countyNumber           (:county_number data)
                     :countyName             (:county_name data)
                     :licenseTypeCode        (:license_type_code data)
                     :licenseNumber          (:license_number data)
                     :businessName           (:business_name data)
                     :inspectionDate    (timef/unparse (timef/formatter "YYYY-MM-dd")
                                          (coerce-time/from-date (:inspection_date data)))
                     :locationAddress        (:location_address data)
                     :locationCity           (:location_city data)
                     :locationZipcode        (:location_zipcode data)
                     :inspectionNumber       (:inspection_number data)
                     :visitNumber            (:visit_number data)
                     :inspectionType         (:inspection_type data)
                     :inspectionDisposition  (:inspection_disposition data)
                     :totalViolations        (:total_violations data)
                     :highPriorityViolations (:high_priority_violations data)
                     :intermediateViolations (:intermediate_violations data)
                     :basicViolations        (:basic_violations data)}]
     (if is-full
       (assoc basic-data
              :criticalViolationsBefore2013      (:critical_violations_before_2013 data)
              :nonCriticalViolationsBefore2013   (:noncritical_violations_before_2013 data)
              :pdaStatus                         (:pda_status data)
              :licenseId                         (:license_id data)
              :violations                        (:violations data))
       basic-data))))

(defn inspections-by-zipcodes
  "Return inspections per given location and period."
  [zips start-date end-date]
  (let [zips (str/split zips #",")]
    (map format-data
         (db/select-inspections-by-location
          {:startDate    start-date
           :endDate      end-date
           :zips         zips}))))

(defn inspections-by-business-name
  "Return inspections per given business name, location and period."
  ([name start-date end-date]
   (map format-data
        (db/select-inspections-by-restaurant
         {:startDate    start-date
          :endDate      end-date
          :businessName (str/replace name #"\*" "%")})))
  ([name zips start-date end-date]
   (let [zips (str/split zips #",")]
     (map format-data
          (db/select-inspections-by-restaurant-location
           {:startDate    start-date
            :endDate      end-date
            :businessName (str/replace name #"\*" "%")
            :zips         zips})))))

(defn inspections-by-district
  "Return inspections per given district and period."
  [district start-date end-date]
  (map format-data
       (db/select-inspections-by-district
        {:startDate start-date
         :endDate   end-date
         :district  district}))

  (defn inspections-by-county
    "Return inspections per given county and period."
    [countyNumber start-date end-date]
    (map format-data
         (db/select-inspections-by-county
          {:startDate    start-date
           :endDate      end-date
           :countyNumber countyNumber}))))

(defn violations-for-inspection
  "Select and parse violations for a given inspection id."
  [inspection-id]
  (map (fn [violation]
         {:id               (:violation_id violation)
          :count            (:violation_count violation)
          :description      (:description violation)
          :isRiskFactor     (:is_risk_factor violation)
          :isPrimaryConcern (:is_primary_concern violation)})
       (db/select-violations-by-inspection {:id inspection-id})))

(defn full-inspection-details
  "Return full inspection info for the given Id."
  [id]
  (if-let [inspection (first (db/select-inspection-details {:id id}))]
             (format-data (assoc inspection :violations (violations-for-inspection (:inspection_visit_id inspection)))
                          true)
             (res/not-found)))

(defn inspections-by-all
  ""
  []
  0
  )

(defn get-counties
  "Return counties list, with their district."
  []
  (res/ok (db/select-counties-summary)))
