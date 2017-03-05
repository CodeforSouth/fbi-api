(ns fbi-api.services
  (:require [yesql.core :refer [defqueries]]
            [taoensso.timbre :refer [debug]]
            ;; internal
            [fbi-api.db :as db]
            [fbi-api.util :as util]))

(defn format-inspection
  "Format db raw data for inspections."
  ([data]
   (format-inspection data false))
  ([data is-full]
   (assoc (if is-full
            data
            (dissoc data
                    :critical_violations_before_2013
                    :noncritical_violations_before_2013
                    :license_id
                    :violations))
          :id (:inspection_visit_id data)
          :inspection_date (util/parse-date-or-nil (:inspection_date data)))))

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
  (when-let [inspection (first (db/select-inspection-details {:id id}))]
    [(format-inspection
       (assoc inspection
              :violations (violations-for-inspection (:inspection_visit_id inspection)))
       true)]))

(defn full-business-details
  "Return full business info for the given Id."
  [id]
  (when-let [business (first (db/select-restaurant-details {:licenseNumber id}))]
    [(assoc business :id id)]))

(defn get-violations
  "Retrieve all violations from db."
  []
  (db/select-all-violations))
