(ns restaurant-inspections-api.services-test
  (:require [clojure.test :refer :all]
            [restaurant-inspections-api.services :as srv]
            [restaurant-inspections-api.util :as util]
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

;; Test general helper functions
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
        (is (= (:totalViolations json) 11))))
    ;; TODO: test given full results, gets fully correctly formatted object
    (testing "Given empty results, returns object with all keys nil"
      (is (= (srv/format-data nil) {:basicViolations nil,
                                    :businessName nil,
                                    :countyName nil,
                                    :countyNumber nil,
                                    :district nil,
                                    :highPriorityViolations nil,
                                    :id nil,
                                    :type "inspections"
                                    :inspectionDate nil,
                                    :inspectionDisposition nil,
                                    :inspectionNumber nil,
                                    :inspectionType nil,
                                    :intermediateViolations nil,
                                    :licenseNumber nil,
                                    :licenseTypeCode nil,
                                    :locationAddress nil,
                                    :locationCity nil,
                                    :locationZipcode nil,
                                    :totalViolations nil,
                                    :visitNumber nil})))))
(deftest format-params-test
  (testing "Given parameters, returns formatted params"
  (is (= { :zipCodes ["326015125"],
           :businessName "%MC%DON%",
           :startDate "2013-01-01",
           :endDate "2016-11-06",
           :districtCode nil,
           :countyNumber nil,
           :perPage 20,
           :page 0
          } (srv/format-params { :zipCodes "326015125",
                                         :businessName "*MC*DON*",
                                         :startDate "2013-01-01",
                                         :endDate "2016-11-06",
                                         :districtCode nil,
                                         :countyNumber nil,
                                         :perPage 20,
                                :page 0 }))))
  (testing "given per page and page number, returns page multiplied by perpage"
    (is (= { :zipCodes ["326015125"],
            :businessName "%MC%DON%",
            :startDate "2013-01-01",
            :endDate "2016-11-06",
            :districtCode nil,
            :countyNumber nil,
            :perPage 10,
            :page 20
            }
           (srv/format-params { :zipCodes "326015125",
                               :businessName "*MC*DON*",
                               :startDate "2013-01-01",
                               :endDate "2016-11-06",
                               :districtCode nil,
                               :countyNumber nil,
                               :perPage 10,
                               :page 2 })
           )))
  )
