(ns restaurant-inspections-api.handlers.inspections-test
  (:require [clojure.test :refer :all]
            [restaurant-inspections-api.handlers.inspections :refer :all]))

(def mock-ctx {:request {:params {:businessName "Johnys Pizza"
                                  :zipCodes "32345"
                                  :district "D3"
                                  :startDate "2013-02-02"
                                  :endDate "2017-02-01"
                                  :countyNumber "19"
                                  :perPage "1"
                                  :page "2"}}})

(deftest processable?-test
  (testing "Given a request object, returns all params if all true."
    (is (= (processable? mock-ctx)
           [true {:valid-params
                  {:zipCodes "32345"
                   :businessName "Johnys Pizza"
                   :startDate "2013-02-02"
                   :endDate "2017-02-01"
                   :districtCode "D3"
                   :countyNumber "19"
                   :perPage 1
                   :page 2}}])))

  (testing "json error for county if county is invalid"
    (is (= [false {:errors-map {:errors [{:code 1, :title "Validation Error", :detail "Invalid format or value for parameter.", :source {:parameter "countyNumber"}}]}, :params {:zipCodes "32345", :businessName "Johnys Pizza", :startDate "2013-02-02", :endDate "2017-02-01", :districtCode "D3", :perPage 1, :page 2}}]
         (processable? (assoc-in mock-ctx [:request :params :countyNumber] "38h3fh__"))
           )))

  (testing "json error for district if district is invalid"
    (is (= [false {:errors-map {:errors [{:code 1, :title "Validation Error", :detail "Invalid format or value for parameter.", :source {:parameter "districtCode"}}]}, :params {:zipCodes "32345", :businessName "Johnys Pizza", :startDate "2013-02-02", :endDate "2017-02-01", :countyNumber "19", :perPage 1, :page 2}}]
         (processable? (assoc-in mock-ctx [:request :params :district] "D9999"))
           )))

  (testing "json error for date if date is invalid"
    (is (= [false {:errors-map {:errors [{:code 1, :title "Validation Error", :detail "Invalid format or value for parameter.", :source {:parameter "startDate"}}]}, :params {:zipCodes "32345", :businessName "Johnys Pizza", :endDate "2017-02-01", :districtCode "D3", :countyNumber "19", :perPage 1, :page 2}}]
         (processable? (assoc-in mock-ctx [:request :params :startDate] "2015-03-a0"))
           )))

  (testing "json error for zipCodes if zipCode is invalid"
    (is (= [false {:errors-map {:errors [{:code 1, :title "Validation Error", :detail "Invalid format or value for parameter.", :source {:parameter "zipCodes"}}]}, :params {:businessName "Johnys Pizza", :startDate "2013-02-02", :endDate "2017-02-01", :districtCode "D3", :countyNumber "19", :perPage 1, :page 2}}]
         (processable? (assoc-in mock-ctx [:request :params :zipCodes] "33136,33435,0"))
           ))))

(deftest validate-inspections-params-test
  (testing "Given query params input, returns valid and invalid format/values."
    (is (=
         (validate-inspections-params
          "33137,22345" "McDonalds" "2013-aa-22" "2015-03-03" nil nil "1" "2")
         {:invalid {:startDate false}
          :valid {:zipCodes "33137,22345"
                  :businessName "McDonalds"
                  :endDate "2015-03-03"
                  :districtCode nil
                  :countyNumber nil
                  :perPage 1
                  :page 2}}))))


;; TODO: inspections-ok still under routes.clj and under construction
(deftest handle-ok-test
  (testing "handles all params scenario"))

(deftest handle-unprocessable-test
  (testing "properly returns correct error object given a ctx"))
