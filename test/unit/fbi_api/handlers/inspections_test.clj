(ns unit.fbi-api.handlers.inspections-test
  (:require [clojure.test :refer :all]
            [fbi-api.handlers.inspections :refer :all]))

;; =========================== MOCK DATA ===================================

(def mock-ctx {:request {:params {:businessName "Johnys Pizza"
                                  :zipCodes "32345"
                                  :districtCode "D3"
                                  :startDate "2013-02-02"
                                  :endDate "2017-02-01"
                                  :countyNumber "19"
                                  :perPage "1"
                                  :page "2"}}})

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

;; ================================ BEGIN TESTS ============================

(deftest processable?-test
  (testing "Given a request object, returns all params if all true."
    (is (= (processable? mock-ctx)
           [true {:valid-params
                  {:zipCodes "32345"
                   :businessName "Johnys Pizza"
                   :startDate "2013-02-02"
                   :endDate "2017-02-01"
                   :countyNumber "19"
                   :districtCode "D3"
                   :perPage 1
                   :page 2}}])))

  (testing "json error for county if county is invalid"
    (is (= [false
            {:errors-map {:errors [{:code 1,
                                    :title "Validation Error",
                                    :detail "Invalid format or value for parameter.",
                                    :source {:parameter "countyNumber"}}]},
             :params {:zipCodes "32345",
                      :businessName "Johnys Pizza",
                      :startDate "2013-02-02",
                      :endDate "2017-02-01",
                      :districtCode "D3",
                      :perPage 1,
                      :page 2}}]
           (processable? (assoc-in mock-ctx [:request :params :countyNumber] "38h3fh__")))))

  (testing "json error for district if district is invalid"
    (is (= [false {:errors-map {:errors [{:code 1,
                                          :title "Validation Error",
                                          :detail "Invalid format or value for parameter.",
                                          :source {:parameter "districtCode"}}]},
                   :params {:zipCodes "32345",
                            :businessName "Johnys Pizza",
                            :startDate "2013-02-02",
                            :endDate "2017-02-01",
                            :countyNumber "19",
                            :perPage 1,
                            :page 2}}]
           (processable? (assoc-in mock-ctx [:request :params :districtCode] "D9999")))))

  (testing "json error for date if date is invalid"
    (is (= [false {:errors-map {:errors [{:code 1,
                                          :title "Validation Error",
                                          :detail "Invalid format or value for parameter.",
                                          :source {:parameter "startDate"}}]},
                   :params {:zipCodes "32345",
                            :businessName "Johnys Pizza",
                            :endDate "2017-02-01",
                            :districtCode "D3",
                            :countyNumber "19",
                            :perPage 1,
                            :page 2}}]
           (processable? (assoc-in mock-ctx [:request :params :startDate] "2015-03-a0")))))

  (testing "json error for zipCodes if zipCode is invalid"
    (is (= [false {:errors-map {:errors [{:code 1,
                                          :title "Validation Error",
                                          :detail "Invalid format or value for parameter.",
                                          :source {:parameter "zipCodes"}}]},
                   :params {:businessName "Johnys Pizza",
                            :startDate "2013-02-02",
                            :endDate "2017-02-01",
                            :districtCode "D3",
                            :countyNumber "19",
                            :perPage 1,
                            :page 2}}]
           (processable? (assoc-in mock-ctx [:request :params :zipCodes] "33136,33435,0"))))))

(deftest validate-inspections-params-test
  (testing "Given query params input, returns valid and invalid format/values."
    (is (= {:invalid {:startDate false}
            :valid {:zipCodes "33137,22345"
                    :businessName "McDonalds"
                    :endDate "2015-03-03"
                    :districtCode nil
                    :countyNumber nil
                    :perPage 1
                    :page 2}}
           (validate-inspections-params {:zipCodes "33137,22345"
                                         :businessName "McDonalds"
                                         :startDate "2013-aa-22"
                                         :endDate "2015-03-03"
                                         :districtCode nil
                                         :countyNumber nil
                                         :perPage "1"
                                         :page "2"})))))

(deftest handle-unprocessable-test
  (testing "properly returns correct error object given a ctx"))

(deftest format-params-test
  (testing "Given parameters, returns formatted params"
    (is (= {:zipCodes ["326015125"],
            :businessName "%MC%DON%",
            :startDate "2013-01-01",
            :endDate "2016-11-06",
            :districtCode nil,
            :countyNumber nil,
            :perPage 20,
            :page 0} (format-params {:zipCodes "326015125",
                                     :businessName "*MC*DON*",
                                     :startDate "2013-01-01",
                                     :endDate "2016-11-06",
                                     :districtCode nil,
                                     :countyNumber nil,
                                     :perPage 20,
                                     :page 0}))))
  (testing "given per page and page number, returns page multiplied by perpage"
    (is (= {:zipCodes ["326015125"],
            :businessName "%MC%DON%",
            :startDate "2013-01-01",
            :endDate "2016-11-06",
            :districtCode nil,
            :countyNumber nil,
            :perPage 10,
            :page 20}
           (format-params {:zipCodes "326015125",
                           :businessName "*MC*DON*",
                           :startDate "2013-01-01",
                           :endDate "2016-11-06",
                           :districtCode nil,
                           :countyNumber nil,
                           :perPage 10,
                           :page 2})))))

(deftest format-inspection-test
  ;; TODO: test given full results, gets fully correctly formatted object
  ;; TODO: given empty, returns full object with nulls
  (let [data inspection-example]
    (testing "Basic data"
      (let [json (format-inspection data)]
        (is (= (:location_city json) "COOPER CITY"))
        (is (= (:id json) 5807286))
        (is (= (:total_violations json) 11))))
    (testing "Full data"
      (let [json (format-inspection data true)]
        (is (= (:location_city json) "COOPER CITY"))
        (is (= (count (:violations json)) 8))
        (is (= (first (:violations json)) {:id 3, :count 1}))
        (is (= (:total_violations json) 11))))))
