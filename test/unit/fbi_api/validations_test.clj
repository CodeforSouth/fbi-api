(ns unit.fbi-api.validations-test
  (:require [clojure.test :refer :all]
            [fbi-api.validations :as validate]))

;; TODO: test for failure (pass integers, maps, vectors and see error)
;; TODO: add pre conditions to impl of these functions to throw a
;;       consistent assertion error if fails.

(deftest validate-zip-codes-test
  (testing "Given a string zipcode list, passes through/returns value if valid."
    (is (= "00953,32978,33137" (validate/zip-codes "00953,32978,33137")))
    (is (= "09878" (validate/zip-codes "09878")))
    (is (= "098783452" (validate/zip-codes "098783452")))
    ;; will allow users to enter 5 zeroes... their bad if nothing matches in DB
    (is (= "00000" (validate/zip-codes "00000"))))
  (testing "Given invalid zipCodes format, returns false. Includes using non-accepted zipcode characters or difference in required digits."
    (is (false? (validate/zip-codes "00946,a3456")))
    (is (false? (validate/zip-codes "")))
    (is (false? (validate/zip-codes "a3433")))
    (is (false? (validate/zip-codes "[]")))
    (is (false? (validate/zip-codes "1234")))
    (is (false? (validate/zip-codes "123456"))))
  (testing "Returns nil if zipcodes not provided (is nil)"
    (is (nil? (validate/zip-codes nil))))
  (testing "Given wrong param type, raises exception"
    ;; java.lang.ClassCastException
    ;; TODO: could be an AssertionError
    ;; (is (= 5 (validate/zip-codes '())))
))

(deftest validate-date-test
  (testing "Given a date string in (valid is YYYY-MM-DD) format, returns back value for valid date and format"
    (is (= "2013-01-01" (validate/date "2013-01-01")))
    (is (= "2017-02-02" (validate/date "2017-02-02")))
    (is (= "1988-12-31" (validate/date "1988-12-31"))))
  (testing "Returns false for invalid date or format"
    (is (false? (validate/date "0000-00-00")))
    (is (false? (validate/date "hello")))
    (is (false? (validate/date "20a0-10-10")))
    (is (false? (validate/date "2034")))
    (is (false? (validate/date "9")))))

(deftest validate-district-code-test
  (testing "True if a valid district code"
    (is (= "D2" (validate/district-code "D2")))
    (is (= "D12" (validate/district-code "D12")))
    (is (= "D1" (validate/district-code "D1"))))
  (testing "False if an invalid district code"
    (is (false? (validate/district-code "2034")))
    (is (false? (validate/district-code "D")))
    (is (false? (validate/district-code "D123"))))
  (testing "Nil if district code is nil"
    (is (nil? (validate/district-code nil)))))

(deftest validate-county-number-test
  (testing "True if a valid county number"
    (is (= "123" (validate/county-number "123")))
    (is (= "12" (validate/county-number "12")))
    (is (= "01" (validate/county-number "01")))
    (is (= "6" (validate/county-number "6"))))
  (testing "False if an invalid county number"
    (is (false? (validate/county-number "2034")))
    (is (false? (validate/county-number "D")))
    (is (false? (validate/county-number "D123"))))
  (testing "Nil if county number is nil"
    (is (nil? (validate/county-number nil)))))

(deftest validate-per-page-test
  (testing "Given a valid per page input, returns value"
    (is (= 12 (validate/per-page "12"))))
  (testing "Given an invalid per page input, returns false"
    (is (= false (validate/per-page "a"))))
  (testing "Given nil, returns nil"
    (is (nil? (validate/per-page nil)))))

(deftest validate-page-test
  (testing "Given a valid page input, returns value"
    (is (= 12 (validate/page "12"))))
  (testing "Given an invalid page input, returns false"
    (is (= false (validate/page "a"))))
  (testing "Given nil, returns nil"
    (is (nil? (validate/page nil)))))
