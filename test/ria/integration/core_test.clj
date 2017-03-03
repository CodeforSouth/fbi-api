(ns ria.integration.core-test
  (:require [clojure.test :refer :all]
            [restaurant-inspections-api.core :refer :all]))

(deftest ^{:integration true} -main-test
  (testing " main "
    (println "integrationn")
    (is (= 3 3))))
