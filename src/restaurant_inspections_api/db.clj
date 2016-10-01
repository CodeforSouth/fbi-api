(ns restaurant-inspections-api.db
    (:require [yesql.core :refer [defquery]]
              [restaurant-inspections-api.environment :as env]))

(def db-params {:connection (env/get-env-db-url)})

; Insert Statements
(defquery insert-county! "sql/insert_county.sql" db-params)
(defquery insert-restaurant! "sql/insert_restaurant.sql" db-params)
(defquery insert-inspection! "sql/insert_inspection.sql" db-params)
(defquery insert-inspection-violation! "sql/insert_inspection_violation.sql" db-params)

; Queries
(defquery select-inspection-details "sql/select_inspection_details.sql" db-params)
(defquery select-violations-by-inspection "sql/select_violations_by_inspection_id.sql" db-params)
(defquery select-counties-summary "sql/select_counties_summary.sql" db-params)
(defquery select-inspections-by-county "sql/select_inspections_by_county.sql" db-params)
(defquery select-inspections-by-district "sql/select_inspections_by_district.sql" db-params)
(defquery select-inspections-by-location "sql/select_inspections_by_location.sql" db-params)
(defquery select-inspections-by-restaurant "sql/select_inspections_by_restaurant.sql" db-params)
(defquery select-inspections-by-restaurant-location "sql/select_inspections_by_restaurant_location.sql" db-params)
