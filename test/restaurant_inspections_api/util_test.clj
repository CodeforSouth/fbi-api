(ns restaurant-inspections-api.util-test
  (:require [clojure.test :refer :all]
            [restaurant-inspections-api.util :refer :all]))

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
