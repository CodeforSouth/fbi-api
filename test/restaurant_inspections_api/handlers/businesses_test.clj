(ns restaurant-inspections-api.handlers.businesses-test
  (:require [clojure.test :refer :all]
            [restaurant-inspections-api.handlers.businesses :refer :all]))

(def mock-ctx {:request {:params {:zipCodes "32345"
                                  :countyNumber "19"
                                  :perPage "1"
                                  :page "2"}}})

(deftest validate-businesses-params-test
  (testing "Given query params input, returns valid and invalid format/values."
    (is (= {:invalid {}
          :valid {:zipCodes "33137,22345"
                  :countyNumber "11"
                  :perPage 1
                  :page 2}}
         (validate-businesses-params
          "33137,22345" "11" "1" "2")))))
