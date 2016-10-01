(ns restaurant-inspections-api.services-test
    (:require [clojure.test :refer :all]
              [cheshire.core :refer [parse-string]]
              [restaurant-inspections-api.services :as srv]
              [restaurant-inspections-api.db :as db]))

(def inspection-example
    {:county_name "Broward",
     :county_number 16,
     :location_address "8735 STIRLING RD",
     :noncritical_violations_before_2013 nil,
     :license_type_code "2010",
     :location_city "COOPER CITY",
     :business_name "FAMILY BAGELS OF LONG ISLAND",
     :inspection_date #inst "2016-07-07T04:00:00.000-00:00",
     :pda_status true,
     :license_number 1621404,
     :location_zipcode "33328",
     :critical_violations_before_2013 nil,
     :high_priority_violations 2,
     :inspection_visit_id 5807286,
     :inspection_number 2571280,
     :basic_violations 4,
     :license_id "6321820",
     :inspection_class "Food",
     :total_violations 11,
     :inspection_disposition "Inspection Completed - No Further Action",
     :district "D2",
     :intermediate_violations 5,
     :inspection_type "Food-Licensing Inspection",
     :violations [{:id 3, :count 1}
                  {:id 9, :count 1}
                  {:id 18, :count 1}
                  {:id 23, :count 1}
                  {:id 33, :count 1}
                  {:id 43, :count 1}
                  {:id 53, :count 1}
                  {:id 54, :count 1}]})

; Test general helper functions
(deftest format-data-test
    (let [data inspection-example]
        (testing "Basic data"
            (let [json (srv/format-data data)]
                (is (= (:locationCity json) "COOPER CITY"))
                (is (= (:id json) 5807286))
                (is (= (:totalViolations json) 11))))
        (testing "Full data"
            (let [json (srv/format-data data true)]
                (is (= (:locationCity json) "COOPER CITY"))
                (is (= (count (:violations json)) 8))
                (is (= (first (:violations json)) {:id 3, :count 1}))
                (is (= (:totalViolations json) 11))))))

(deftest inspections-by-zipcodes-test
    (testing "Works correctly upon receiving all parameters")
    (testing "Works when receiving only zips and uses default date range if so"
        (is (=
                (with-redefs [db/select-inspections-by-location {}]
                    (srv/inspections-by-zipcodes "33129"))
                {:status 200
                 :headers {"Content-Type" "application/json"}
                 :body "[]"}
                ))))

(deftest inspections-by-business-name-test
    (testing "Works correctly upon receiving all parameters")
    (testing "Works when receiving only business name and uses default date range if so"
        (is (=
                (with-redefs [db/select-inspections-by-restaurant {}]
                    (srv/inspections-by-business-name "McDonalds"))
                {:status 200, :headers {"Content-Type" "application/json"}, :body "[]"}
                ))))

(deftest inspections-by-district-test
    (testing "Works correctly upon receiving all parameters")
    (testing "Works when receiving only district name and uses default date range if so"
        (is (=
                (with-redefs [db/select-inspections-by-district {}]
                    (srv/inspections-by-district "whoknows"))
                {:status 200, :headers {"Content-Type" "application/json"}, :body "[]"}
                ))))

(deftest inspections-by-county-test
    (testing "Works correctly upon receiving all parameters")
    (testing "Works when receiving only district name and uses default date range if so"
        (is (=
                (with-redefs [db/select-inspections-by-county {}]
                    (srv/inspections-by-county "another"))
                {:status 200, :headers {"Content-Type" "application/json"}, :body "[]"}
                ))))

