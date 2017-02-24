(ns restaurant-inspections-api.handlers.inspections
  (:require [restaurant-inspections-api.validations :as validate]
            [restaurant-inspections-api.util :as util]
            [restaurant-inspections-api.db :as db]))

(defn validate-inspections-params
  "Receives all inspections query parameters (nil when not specified) and returns a map of valid and
  invalid params."
  [zip-codes business-name start-date end-date district-code county-number per-page page]

  (validate/validate-params {:zipCodes (validate/zip-codes zip-codes)
                             :businessName business-name
                             :startDate (validate/date start-date)
                             :endDate (validate/date end-date)
                             :districtCode (validate/district-code district-code)
                             :countyNumber (validate/county-number county-number)
                             :perPage (validate/per-page per-page)
                             :page (validate/page page)}))

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

(defn format-inspections
  "Format db raw data to json."
  ([data]
   (format-inspections data false))
  ([data is-full]
   (let [basic-data {:type                   "inspections"
                     :id                     (:inspection_visit_id data)
                     :district               (:district data)
                     :countyNumber           (:county_number data)
                     :countyName             (:county_name data)
                     :licenseTypeCode        (:license_type_code data)
                     :licenseNumber          (:license_number data)
                     :businessName           (:business_name data)
                     :inspectionDate         (util/parse-date-or-nil (:inspection_date data))
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

(defn processable?
  "Given a ring server context, returns true or false if parameters are valid/processable. Also sets result (errors, correct fields) into the context."
  [ctx]
  ; lets make a map of field names, values, and valid
  (let [zip-codes (get-in ctx [:request :params :zipCodes])
        business-name (or (get-in ctx [:request :params :businessName]) nil)
        start-date (or (get-in ctx [:request :params :startDate]) "2013-01-01")
        end-date (or (get-in ctx [:request :params :endDate]) (util/todays-date))
        district-code (get-in ctx [:request :params :district])
        county-number (get-in ctx [:request :params :countyNumber])
        per-page (or (get-in ctx [:request :params :perPage]) "20")
        page (or (get-in ctx [:request :params :page]) "0")
        validations-map (validate-inspections-params zip-codes business-name
                                                     start-date end-date
                                                     district-code
                                                     county-number
                                                     per-page
                                                     page)]
    (if-not (empty? (:invalid validations-map))
      [false {:errors-map
              {:errors  ;; for each invalid-params here
               (into [] (for [keyval (:invalid validations-map)]
                          (util/format-query-params-error (name (key keyval)))))}
              :params (:valid validations-map)}]
      [true {:valid-params (:valid validations-map)}])))

(defn format-params
  "Format all the pre-params sent to this endpoint"
  [params-map]
  (assoc params-map
         :businessName (when-let [businessName (:businessName params-map)]
                         (clojure.string/replace businessName #"\*" "%"))
         :zipCodes (when-let [zipCodes (:zipCodes params-map)]
                     (clojure.string/split zipCodes #","))
         :page (* (:page params-map) (:perPage params-map))))

(defn inspections-by-all
  "Retrieves and formats inspections, filtered by all, any, or no criteria."
  [params-map]
  (map format-inspections (db/select-inspections-by-all (format-params params-map))))

(defn full-details
  "Return full inspection info for the given Id."
  [id]
  (if-let [inspection (first (db/select-inspection-details {:id id}))]
    (format-inspections (assoc inspection :violations (violations-for-inspection (:inspection_visit_id inspection)))
                        true)))

(defn handle-ok
  "Handles 200 OK for inspections"
  [{:keys [valid-params]}] ;; as ctx
  {:meta {:parameters valid-params}
   :data (into [] (inspections-by-all valid-params))})

(defn handle-unprocessable
  "Handles 422 Unprocessable when invalid params provided"
  [ctx]
  (merge {:meta
          {:parameters (get ctx :params)}}
         (get ctx :errors-map)))
