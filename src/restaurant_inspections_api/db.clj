(ns restaurant-inspections-api.db
  (:require [yesql.core :refer [defquery]]
            [clojure.java.jdbc :as jdbc]
            [restaurant-inspections-api.constants :as const]))

(def db-params {:connection const/db-url})

; Insert Statements
(defquery insert-county! "sql/insert_county.sql" db-params)
(defquery insert-restaurant! "sql/insert_restaurant.sql" db-params)
(defquery insert-inspection! "sql/insert_inspection.sql" db-params)
(defquery insert-inspection-violation! "sql/insert_inspection_violation.sql" db-params)

; Queries
(defquery select-inspection-details "sql/select_inspection_details.sql" db-params)
(defquery select-violations-by-inspection "sql/select_violations_by_inspection_id.sql" db-params)
(defquery select-counties-summary "sql/select_counties_summary.sql" db-params)
(defquery select-all-restaurants "sql/select_all_restaurants.sql" db-params)
(defquery select-all-violations "sql/select_all_violations.sql" db-params)
;; (defquery select-inspections-by-county "sql/select_inspections_by_county.sql" db-params)
;; (defquery select-inspections-by-district "sql/select_inspections_by_district.sql" db-params)
;; (defquery select-inspections-by-location "sql/select_inspections_by_location.sql" db-params)
;; (defquery select-inspections-by-restaurant "sql/select_inspections_by_restaurant.sql" db-params)
;; (defquery select-inspections-by-restaurant-location "sql/select_inspections_by_restaurant_location.sql" db-params)
;; (defquery select-inspections-by-all "sql/select_inspections_by_all.sql" db-params)

;; FIXME Temporary query, see below function
(def start-query
  (str "SELECT i.inspection_visit_id, i.inspection_number, i.visit_number, i.inspection_class, i.inspection_type, i.inspection_disposition, i.inspection_date, i.total_violations, i.high_priority_violations, i.intermediate_violations, i.basic_violations, i.county_number, i.license_number, r.county_number, r.license_type_code, r.license_number, r.business_name, r.location_address, r.location_city, r.location_zipcode, r.location_latitude, r.location_longitude, c.county_name, c.district FROM inspections AS i INNER JOIN counties AS c ON c.county_number = i.county_number INNER JOIN restaurants AS r ON r.county_number = i.county_number AND r.license_number = i.license_number WHERE i.inspection_date BETWEEN"))

;; TODO: use a prepared statement here
(defn build-select-inspections-query
  "Temporary solution that, given variable number of query params (but dates and page params preset) returns a query in string format."
  [params-map]
  (str start-query
       (str " \"" (:startDate params-map) "\" AND \"" (:endDate params-map) "\"")
       (when-let [zipCodes (:zipCodes params-map)]
         (str " AND r.location_zipcode IN ("
              (if (.contains zipCodes ",")
                (str "\"" (clojure.string/replace zipCodes #"," "\" \"") "\"")
                zipCodes)
              ")"))
       (when-let [businessName (:businessName params-map)]
         (str " AND r.business_name LIKE \""
              (clojure.string/replace businessName #"\*" "%")
              "\""))
       (when-let [districtCode (:districtCode params-map)]
         (str " AND c.district = \"" districtCode "\""))
       (when-let [countyNumber (:countyNumber params-map)]
         (str " AND i.county_number = " countyNumber))
       (str " LIMIT " (:perPage params-map))
       (str " OFFSET " (:page params-map) ";")))

;; TODO: Try to use YesSQL for above query and reimplement select inspections
(defn select-inspections-by-all
  "Returns a list of inspetions from database, filtered by multiple parameters."
  [params-map]
  (jdbc/query const/db-url
              (build-select-inspections-query params-map)))
