(ns restaurant-inspections-api.tasks-test
  (:require [clojure.test :refer :all]
            [restaurant-inspections-api.tasks :as t]))

(deftest str-null->int
  (testing "Check String to Int Nullable Formatter"
    (is (= (t/str-null->int "1") 1))
    (is (= (t/str-null->int "") nil))
    (is (= (t/str-null->int nil) nil))
    (is (= (t/str-null->int "123") 123))))

(deftest str-csv-date->iso
  (testing "Check CSV Date to ISO Formatter"
    (is (= (t/str-csv-date->iso "08/05/2016") "2016-08-05"))
    (is (= (t/str-csv-date->iso "06/31/2016") nil))
    (is (= (t/str-csv-date->iso "123132") nil))))

(deftest parse
  (testing "Parsing CSV data into Restaurants Database Data")
  (let [csv-row "\"D1\",\"23\",\"Dade\",\"2010\",2300098,\"LA PUPUSA FACTORY 2\",\"1947 WEST FLAGLER ST\",\"MIAMI\",\"33135\",2470437,2,\"Food\",\"Routine - Food\",\"Call Back - Extension given, pending\",\"07/23/2015\",,,21,3,5,13,\"Y\",2,0,1,0,0,0,0,1,0,1,0,0,0,4,0,1,0,0,0,0,0,1,2,0,1,0,0,0,1,0,0,1,2,0,0,3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3791760,5564275"
        db-row (first (t/parse csv-row))]
    (is (= (:inspection_visit_id db-row) 5564275))
    (is (= (:county_name db-row) "Dade"))
    (is (= (:location_address db-row) "1947 WEST FLAGLER ST"))))
