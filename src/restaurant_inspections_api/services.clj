(ns restaurant-inspections-api.services
  (:require [restaurant-inspections-api.responses :as res]
            [restaurant-inspections-api.environment :as env]
            [clojure.java.jdbc :as db]))

(def db-url (env/get-env-db-url))

(defn home
  "go to project wiki"
  []
  (res/redirect "https://github.com/Code-for-Miami/restaurant-inspections-api/wiki"))

(defn top10
  "return the top 10 worse (most violations) inspections"
  []
  (res/ok (db/query db-url
                    ["SELECT id, district, county_name, business_name, license_type_code,
                             license_number, inspection_number, visit_number, inspection_date,
                             inspection_class, inspection_type, inspection_disposition,
                             location_address, location_city, location_zipcode,
                             total_violations, high_priority_violations, intermediate_violations,
                             basic_violations
                        FROM restaurant_inspections
                       ORDER BY total_violations DESC
                       LIMIT 10"])))