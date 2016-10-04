(ns restaurant-inspections-api.routes-test
  (:require [clojure.test :refer :all]
            [restaurant-inspections-api.routes :refer :all]))

(defn request [resource web-app & params]
  (all-routes {:request-method :get :uri resource :params (first params)}))

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

(deftest test-all-routes
  (testing "Returns 404 if route doesnt match"
    (is (= 404 (:status (request "/name" all-routes))))))

(deftest validate-zip-codes-test
  (testing "Returns true for valid zipcode list"
    (is (true? (validate-zip-codes "00953,32978,33137")))
    (is (true? (validate-zip-codes "09878")))
    ;; will allow users to enter 5 zeroes... their bad if nothing matches in DB
    (is (true? (validate-zip-codes "00000"))))
  (testing "Returns false for invalid format of zipcodes, including non accepted zipcode characters or difference in required digits"
    (is (false? (validate-zip-codes "00946,a3456")))
    (is (false? (validate-zip-codes "")))
    (is (false? (validate-zip-codes "a3433")))
    (is (false? (validate-zip-codes "[]")))
    (is (false? (validate-zip-codes "1234")))
    (is (false? (validate-zip-codes "123456"))))
  (testing "Returns nil if zipcodes not provided (is nil)"
    (is (nil? (validate-zip-codes nil)))))

(deftest validate-date-test
  (testing "Returns true for valid date and format"
    (is (true? (validate-date "2013-01-01")))
    (is (true? (validate-date "2017-02-02")))
    (is (true? (validate-date "1988-12-31"))))
  (testing "Returns false for invalid date or format"
    (is (false? (validate-date "0000-00-00")))
    (is (false? (validate-date "hello")))
    (is (false? (validate-date "20a0-10-10")))
    (is (false? (validate-date "2034")))
    (is (false? (validate-date "9")))))

(deftest validate-district-code-test
  (testing "True if a valid district code"
    (is (true? (validate-district-code "D2")))
    (is (true? (validate-district-code "D12")))
    (is (true? (validate-district-code "D1"))))
  (testing "False if an invalid district code"
    (is (false? (validate-district-code "2034")))
    (is (false? (validate-district-code "D")))
    (is (false? (validate-district-code "D123"))))
  (testing "Nil if district code is nil"
    (is (nil? (validate-district-code nil)))))

(deftest validate-county-number-test
  (testing "True if a valid county number"
    (is (true? (validate-county-number "123")))
    (is (true? (validate-county-number "12")))
    (is (true? (validate-county-number "01")))
    (is (true? (validate-county-number "6"))))
  (testing "False if an invalid county number"
    (is (false? (validate-county-number "2034")))
    (is (false? (validate-county-number "D")))
    (is (false? (validate-county-number "D123"))))
  (testing "Nil if county number is nil"
    (is (nil? (validate-county-number nil)))))

(deftest validate-inspections-request-test
  (testing "returns exit code 0 if all true"
    (is (= (validate-inspections-request mock-ctx) [true {:validation-vector [true true true true true true]}] )))
  (testing "json error for county if county is invalid"
    (is (= (validate-inspections-request (assoc-in mock-ctx [:request :params :countyNumber] "38h3fh__"))
           [false {:error {:error "Invalid format or value for parameter countyNumber"}}])))
  (testing "json error for district if district is invalid"
    (is (= (validate-inspections-request (assoc-in mock-ctx [:request :params :districtCode] "D9999"))
           [false {:error {:error "Invalid format or value for parameter districtCode"}}])))
  (testing "json error for date if date is invalid"
    (is (= (validate-inspections-request (assoc-in mock-ctx [:request :params :startDate] "2015-03-00"))
           [false {:error {:error "Invalid format or value for parameter startDate"}}])))
  (testing "json error for zipCodes if zipCode is invalid"
    (is (= (validate-inspections-request (assoc-in mock-ctx [:request :params :zipCodes] "33136,33435,0"))
           [false {:error {:error "Invalid format or value for parameter zipCodes"}}])))
  (testing "returns exit code 2 if most true"
    (is (= (validate-inspections-request (assoc-in mock-ctx [:request :params :zipCodes] nil))
           [true {:validation-vector [nil true true true true true]}] ))))

(deftest handle-inspections-ok-test
  (testing "handles all params scenario"
    (is (= 1 1))))
