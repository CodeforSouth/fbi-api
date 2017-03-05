(ns fbi-api.handlers.inspections
  (:require [fbi-api.validations :as validate]
            [fbi-api.util :as util]
            [fbi-api.db :as db]
            [fbi-api.constants :as constants]))

(defn validate-inspections-params
  "Receives all inspections query parameters (nil when not specified) and returns a map of valid and
  invalid params."
  [{:keys [zipCodes businessName startDate endDate districtCode countyNumber perPage page]}]
  (validate/validate-params {:zipCodes (validate/zip-codes zipCodes)
                             :businessName (or businessName nil)
                             :startDate (validate/date (or startDate constants/earliest-date))
                             :endDate (or (validate/date endDate) (util/todays-date))
                             :districtCode (validate/district-code districtCode)
                             :countyNumber (validate/county-number countyNumber)
                             :perPage (or (validate/per-page perPage) 20)
                             :page (or (validate/page page) 0)}))

(defn format-params
  "Format all the pre-params sent to this endpoint"
  [params-map]
  (assoc params-map
         :businessName (when-let [business-name (:businessName params-map)]
                         (clojure.string/replace business-name #"\*" "%"))
         :zipCodes (when-let [zip-codes (:zipCodes params-map)]
                     (clojure.string/split zip-codes #","))
         :page (* (:page params-map) (:perPage params-map))))

(defn processable?
  [ctx]
  (validate/processable? validate-inspections-params ctx))

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

(defn handle-ok
  "Handles 200 OK for inspections"
  [{:keys [valid-params]}] ;; as ctx
  {:meta {:parameters valid-params}
   :data (into [] (map format-inspection
                       (db/select-inspections-by-all (format-params valid-params))))})

(defn violations-for-inspection
  "Select and parse violations for a given inspection id."
  [inspection-id]
  (map (fn [violation]
         (dissoc (clojure.set/rename-keys violation
                                          {:violation_id :id
                                           :violation_count :count})
                 :inspection_id))
       (db/select-violations-by-inspection {:id inspection-id})))

(defn handle-individual-ok
  "Return full inspection info for the given Id."
  [id]
  {:meta {}
   :data (if-let [inspection (first (db/select-inspection-details {:id id}))]
           [(format-inspection
             (assoc inspection
                    :violations (violations-for-inspection (:inspection_visit_id inspection)))
             true)]
           [])})

(defn handle-unprocessable
  "Handles 422 Unprocessable when invalid params provided"
  [ctx]
  (merge {:meta
          {:parameters (get ctx :params)}}
         (get ctx :errors-map)))
