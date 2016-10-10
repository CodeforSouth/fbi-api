(ns restaurant-inspections-api.db-test
  (:require [clojure.test :refer :all]
            [restaurant-inspections-api.db :refer :all]))

(def params-map {:zipCodes "32345,33136"
                 :businessName "Johnys Pizza"
                 :startDate "2013-02-02"
                 :endDate "2017-02-01"
                 :districtCode "D3"
                 :countyNumber "19"
                 :perPage "20"
                 :page "1"})

(deftest build-select-inspections-query-test
  (testing "Given a params-map with all params present, returns a complete SQL query"
    (is (= (build-select-inspections-query params-map) "SELECT i.inspection_visit_id, i.inspection_number, i.visit_number, i.inspection_class, i.inspection_type, i.inspection_disposition, i.inspection_date, i.total_violations, i.high_priority_violations, i.intermediate_violations, i.basic_violations, i.county_number, i.license_number, r.county_number, r.license_type_code, r.license_number, r.business_name, r.location_address, r.location_city, r.location_zipcode, r.location_latitude, r.location_longitude, c.county_name, c.district FROM inspections AS i INNER JOIN counties AS c ON c.county_number = i.county_number INNER JOIN restaurants AS r ON r.county_number = i.county_number AND r.license_number = i.license_number WHERE i.inspection_date BETWEEN \"2013-02-02\" AND \"2017-02-01\" AND r.location_zipcode IN (\"32345\",\"33136\") AND r.business_name LIKE \"Johnys Pizza\" AND c.district = \"D3\" AND i.county_number = 19 LIMIT 20 OFFSET 1;"))))
