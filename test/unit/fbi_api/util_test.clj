(ns unit.fbi-api.util-test
  (:require [clojure.test :refer :all]
            [fbi-api.util :refer :all]))

(deftest str-null->int-test
  (testing "Should return integer on correct int string"
    (is (= 1 (str-null->int "1")))
    (is (= 43434 (str-null->int "43434")))
    (is (= (str-null->int "123") 123)))
  (testing "Should return null on incorrect int string"
    (is (= nil (str-null->int "a")))
    (is (= nil (str-null->int nil)))
    (is (= nil (str-null->int "")))
    (is (= nil (str-null->int 7)))
    (is (= nil (str-null->int 7.45)))
    (is (= nil (str-null->int 0.7)))))

(deftest str-csv-date->iso-test
  (testing "Converts date string from csv format to iso date for use in db"
    (is (= "2016-01-05" (str-csv-date->iso "01/05/2016")))
    (is (= nil (str-csv-date->iso "06/31/2016")))
    (is (= nil (str-csv-date->iso "123132")))))

(deftest todays-date-test
  (testing "output should be string"
    (is (= (type (todays-date)) java.lang.String)))
  (testing "Format should be date-like"
    (is (not (nil? (re-matches #"\d{4}-\d\d-\d\d" (todays-date)))))))

(deftest parse-date-or-nil-test
  (testing "Given nil, returns nil"
    (is (= nil (parse-date-or-nil nil))))
  ;; TODO:
  (testing "Given a date string, returns a formatted date")
  (testing "Given the wrong data type, raises ClassCastException"
    (is (thrown? java.lang.ClassCastException (parse-date-or-nil {})))))
