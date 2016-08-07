(ns restaurant-inspections-api.services-test
  (:require [clojure.test :refer :all]
            [cheshire.core :refer [parse-string]]
            [restaurant-inspections-api.services :as srv]))

(def inspection-example
  {:violation_06 1,
   :violation_44 0,
   :violation_57 0,
   :violation_58 0,
   :county_name "Broward",
   :violation_51 0,
   :violation_38 0,
   :violation_17 0,
   :violation_36 0,
   :violation_42 0,
   :violation_16 1,
   :violation_23 0,
   :violation_22 2,
   :county_number 16,
   :location_address "8735 STIRLING RD",
   :violation_54 0,
   :violation_07 0,
   :violation_14 0,
   :visit_number 1,
   :violation_09 0,
   :violation_01 0,
   :violation_26 0,
   :noncritical_violations_before_2013 nil,
   :license_type_code "2010",
   :violation_50 0,
   :location_city "COOPER CITY",
   :business_name "FAMILY BAGELS OF LONG ISLAND",
   :violation_20 0,
   :violation_48 0,
   :violation_47 0,
   :violation_37 0,
   :inspection_date #inst "2016-07-07T04:00:00.000-00:00",
   :violation_02 0,
   :violation_11 0,
   :violation_27 0,
   :pda_status true,
   :violation_55 0,
   :license_number 1621404,
   :violation_56 0,
   :violation_15 0,
   :violation_05 0,
   :violation_33 0,
   :violation_49 0,
   :violation_08 2,
   :violation_39 0,
   :location_zipcode "33328",
   :violation_45 0,
   :violation_34 0,
   :critical_violations_before_2013 nil,
   :violation_25 0,
   :violation_40 0,
   :violation_12 0,
   :high_priority_violations 2,
   :violation_10 0,
   :inspection_visit_id 5807286,
   :violation_31 0,
   :inspection_number 2571280,
   :basic_violations 4,
   :violation_35 0,
   :license_id "6321820",
   :violation_32 0,
   :violation_28 0,
   :violation_13 0,
   :inspection_class "Food",
   :total_violations 11,
   :violation_30 0,
   :violation_04 0,
   :inspection_disposition "Inspection Completed - No Further Action",
   :violation_53 1,
   :violation_29 0,
   :violation_24 0,
   :violation_43 0,
   :violation_46 0,
   :violation_19 0,
   :violation_41 1,
   :violation_03 1,
   :violation_18 0,
   :violation_52 0,
   :district "D2",
   :intermediate_violations 5,
   :inspection_type "Food-Licensing Inspection",
   :violation_21 2})

; Test general helper functions
(deftest get-violation-count
  (testing "Check Violation Count Formatter"
    (let [data {:violation_01 4,
                :violation_10 0,
                :violation_54 3}]
      (is (= (srv/get-violation-count data 1) 4))
      (is (= (srv/get-violation-count data 10) 0))
      (is (= (srv/get-violation-count data 54) 3))
      (is (empty? (srv/get-violation-count data 9))))))

(deftest parse-violations
  (testing "Violations List Parser"
    (let [data (srv/parse-violations {:violation_01 4,
                                      :violation_02 0,
                                      :violation_03 3})]
      (is (= (count data) 2))
      (is (= (first data) {:id 1 :count 4}))
      (is (= (last data) {:id 3 :count 3})))))

(deftest db-to-json-formatter
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

(with-redefs [srv/query-inspection-details (fn [_] (vector inspection-example))]
  (testing "Get Details of Inspection Response Endpoint from Mocked Data"
    (let [res (srv/get-details 5807286)
          data (parse-string (:body res) true)]
      (is (= (:status res) 200))
      (is (= (:locationCity data) "COOPER CITY"))
      (is (= (count (:violations data)) 8))
      (is (= (first (:violations data)) {:id 3, :count 1}))
      (is (= (:totalViolations data) 11)))))