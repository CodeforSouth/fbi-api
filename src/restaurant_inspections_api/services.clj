(ns restaurant-inspections-api.services
  (:require [restaurant-inspections-api.responses :as res]
            [clj-time.format :as f]
            [clj-time.coerce :as c]
            [clj-time.core :as t]
            [yesql.core :refer [defqueries]]
            [restaurant-inspections-api.db :as db]
            [clojure.string :as str]))

(defn todays-date
  "Get todays date"
  []
  (f/unparse (f/formatter "yyyy-MM-dd") (t/now)))

(defn home
  "Go to project wiki"
  []
  (res/redirect "https://github.com/Code-for-Miami/restaurant-inspections-api/wiki"))

(defn format-data
  "Formats db raw data to json pattern"
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
               :violations                        (:violations data))
        basic-data))))

(defn location
  "Returns inspections per given location and period"
  ([zips]
   (location zips "2013-01-01" (todays-date)))
  ([zips start-date end-date]
  (let [zips (str/split zips #",")]
    (res/ok (map format-data
                 (db/select-inspections-by-location
                   {:startDate    start-date
                    :endDate      end-date
                    :zips         zips}))))))

(defn business
  "Returns inspections per given business name, location and period"
  ([name]
   (business name "2013-01-01" (todays-date)))
  ([name zips]
   (business name zips "2013-01-01" (todays-date)))
  ([name start-date end-date]
   (res/ok (map format-data
                (db/select-inspections-by-restaurant
                  {:startDate    start-date
                   :endDate      end-date
                   :businessName (str/replace name #"\*" "%")}))))
  ([name zips start-date end-date]
   (let [zips (str/split zips #",")]
     (res/ok (map format-data
                  (db/select-inspections-by-restaurant-location
                    {:startDate    start-date
                     :endDate      end-date
                     :businessName (str/replace name #"\*" "%")
                     :zips         zips}))))))

(defn district
  "Returns inspections per given district and period"
  ([district-id]
   (district district-id "2013-01-01" (todays-date)))
  ([district-id start-date end-date]
  (res/ok (map format-data
               (db/select-inspections-by-district
                 {:startDate start-date
                  :endDate   end-date
                  :district  district-id})))))

(defn county
  "Returns inspections per given county and period"
  ([county-number]
   (county county-number "2013-01-01" (todays-date)))
  ([county-number start-date end-date]
  (res/ok (map format-data
               (db/select-inspections-by-county
                 {:startDate    start-date
                  :endDate      end-date
                  :countyNumber county-number})))))

(defn select-violations
  "Select and parse violations for a given inspection id"
  [inspection-id]
  (map (fn [violation]
         {:id               (:violation_id violation)
          :count            (:violation_count violation)
          :description      (:description violation)
          :isRiskFactor     (:is_risk_factor violation)
          :isPrimaryConcern (:is_primary_concern violation)})
       (db/select-violations-by-inspection {:id inspection-id})))

(defn inspection
  "Returns full info on inspection for a given Id"
  [id]
  (res/ok (if-let [inspection (first (db/select-inspection-details {:id id}))]
            (format-data (assoc inspection :violations (select-violations (:inspection_visit_id inspection)))
                         true)
            (res/not-found))))

(defn get-dist-counties
  "Returns district and counties list"
  []
  (res/ok (db/select-counties-summary)))
