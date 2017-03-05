(ns unit.fbi-api.handlers.businesses-test
  (:require [clojure.test :refer :all]
            [fbi-api.handlers.businesses :refer :all]))

(def mock-ctx {:request {:params {:zipCodes "32345"
                                  :countyNumber "19"
                                  :perPage "1"
                                  :page "2"}}})

(deftest validate-businesses-params-test
  (testing "Given a context returns valid and invalid format/values"
    (is (= {:invalid {}
            :valid {:zipCodes "33139,33327"
                    :countyNumber "23"
                    :perPage 20
                    :page 0}}
           (validate-businesses-params {:zipCodes "33139,33327"
                                        :countyNumber "23"
                                        :perPage "20"
                                        :page "0"})))))
