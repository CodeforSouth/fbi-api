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
(defquery select-inspections-by-all "sql/select_inspections_by_all.sql" db-params)