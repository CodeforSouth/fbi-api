(ns restaurant-inspections-api.handlers.inspections-test
  (:require [clojure.test :refer :all]
            [restaurant-inspections-api.handlers.inspections :refer :all]))

(def mock-ctx {:request {
                         :params {
                                  :businessName "Johnys Pizza"
                                  :zipCodes "32345"
                                  :districtCode "D3"
                                  :startDate "2013-02-02"
                                  :endDate "2017-02-01"
                                  :countyNumber "19"
                                  }
                         }
               })

(deftest inspections-processable?-test
  (testing "Given a request object, returns all params if all true."
    (is (= (inspections-processable? mock-ctx)
           [true {:valid-params
                  {:zipCodes "32345"
                   :businessName "Johnys Pizza"
                   :startDate "2013-02-02"
                   :endDate "2017-02-01"
                   :districtCode "D3"
                   :countyNumber "19"}}])))

  (testing "json error for county if county is invalid"
    (is (= (inspections-processable? (assoc-in mock-ctx [:request :params :countyNumber] "38h3fh__"))
           [false
            {:errors-map
             {:errors [{:title "Validation Error"
                        :detail "Invalid format or value for parameter."
                        :code 1
                        :source {:parameter "countyNumber"}}]}}])))

  (testing "json error for district if district is invalid"
    (is (= (inspections-processable? (assoc-in mock-ctx [:request :params :districtCode] "D9999"))
           [false
            {:errors-map
             {:errors [{:title "Validation Error"
                        :detail "Invalid format or value for parameter."
                        :code 1
                        :source {:parameter "districtCode"}}]}}])))

  (testing "json error for date if date is invalid"
    (is (= (inspections-processable? (assoc-in mock-ctx [:request :params :startDate] "2015-03-00"))
           [false
            {:errors-map
             {:errors [{:title "Validation Error"
                        :detail "Invalid format or value for parameter."
                        :code 1
                        :source {:parameter "startDate"}}]}}])))

  (testing "json error for zipCodes if zipCode is invalid"
    (is (= (inspections-processable? (assoc-in mock-ctx [:request :params :zipCodes] "33136,33435,0"))
           [false
            {:errors-map
             {:errors [{:title "Validation Error"
                        :detail "Invalid format or value for parameter."
                        :code 1
                        :source {:parameter "zipCodes"}}]}}]))))

(deftest validate-inspections-params-test
  (testing "Given query params input, returns valid and invalid format/values."
    (is (=
         (validate-inspections-params
          "33137,22345" "McDonalds" "2013-aa-22" "2015-03-03" nil nil)
         {:invalid {:startDate false}
          :valid {:zipCodes "33137,22345", :businessName "McDonalds", :endDate "2015-03-03"}
          }
         ))))

;; TODO: inspections-ok still under routes.clj and under construction
(deftest handle-inspections-ok-test
  (testing "handles all params scenario"))

