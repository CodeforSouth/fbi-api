(ns restaurant-inspections-api.db-test
  (:require [clojure.test :refer :all]
            [restaurant-inspections-api.db :refer :all]))

(def params-map {:zipCodes "32345,33136"
                 :businessName "Johnys Pizza"
                 :startDate "2013-02-02"
                 :endDate "2017-02-01"
                 :districtCode "D3"
                 :countyNumber "19"
                 :perPage "20"
                 :page "1"})