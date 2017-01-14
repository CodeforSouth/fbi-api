(ns restaurant-inspections-api.cron.csv-to-model-test
  (:require [clojure.test :refer :all]
            [restaurant-inspections-api.cron.csv-to-model :refer :all]
            [restaurant-inspections-api.util :as util]))

                                        ; three rows of csv data
(def test-data '(["D1" "23" "Dade" "2010" "2300006" "SPS RESTAURANT" "1757 NE 2 AVE" "MIAMI" "331321191" "2498647" "2" "Food" "Routine - Food" "Call Back - Complied" "01/05/2016" "" "" "3" "1" "1" "1" "Y" "0" "0" "0" "0" "0" "0" "0" "0" "0" "0" "0" "0" "0" "0" "0" "0" "0" "0" "0" "0" "0" "0" "0" "0" "0" "0" "1" "0" "1" "0" "0" "0" "0" "0" "0" "1" "0" "0" "0" "0" "0" "0" "0" "0" "0" "0" "0" "0" "0" "0" "0" "0" "0" "0" "0" "0" "0" "0" "2153540" "5769863"] ["D1" "23" "Dade" "2010" "2300006" "SPS RESTAURANT" "1757 NE 2 AVE" "MIAMI" "331321191" "2560247" "1" "Food" "Routine - Food" "Warning Issued" "05/10/2016" "" "" "21" "6" "4" "11" "Y" "1" "0" "2" "0" "1" "1" "0" "0" "0" "0" "0" "1" "0" "1" "0" "0" "0" "0" "0" "0" "0" "4" "0" "1" "0" "0" "1" "0" "4" "0" "0" "0" "0" "0" "1" "2" "0" "0" "0" "0" "0" "0" "0" "0" "0" "0" "0" "0" "0" "0" "0" "0" "1" "0" "0" "0" "0" "0" "2153540" "5770307"] ["D1" "23" "Dade" "2010" "2300006" "SPS RESTAURANT" "1757 NE 2 AVE" "MIAMI" "331321191" "2498647" "1" "Food" "Routine - Food" "Warning Issued" "10/28/2015" "" "" "16" "5" "3" "8" "Y" "0" "2" "3" "0" "2" "0" "0" "1" "0" "0" "0" "0" "0" "1" "0" "0" "0" "0" "0" "0" "1" "0" "0" "0" "0" "0" "1" "0" "2" "0" "0" "0" "0" "0" "0" "2" "0" "0" "0" "0" "0" "1" "0" "0" "0" "0" "0" "0" "0" "0" "0" "0" "0" "0" "0" "0" "0" "0" "2153540" "5606807"]))

(def map-result {:violation_06 0
                 :violation_44 0
                 :violation_57 0
                 :violation_58 0
                 :county_name "Dade"
                 :violation_51 0
                 :violation_38 0
                 :violation_17 0
                 :violation_36 1
                 :violation_42 0
                 :violation_16 0
                 :violation_23 0
                 :violation_22 0
                 :county_number 23
                 :location_address "1757 NE 2 AVE"
                 :violation_54 0
                 :violation_07 0
                 :violation_14 0
                 :visit_number 2
                 :violation_09 0
                 :violation_01 0
                 :violation_26 0
                 :noncritical_violations_before_2013 nil
                 :license_type_code "2010"
                 :violation_50 0
                 :location_city "MIAMI"
                 :business_name "SPS RESTAURANT"
                 :violation_20 0
                 :violation_48 0
                 :violation_47 0
                 :violation_37 0
                 :inspection_date "2016-01-05"
                 :violation_02 0
                 :violation_11 0
                 :violation_27 1
                 :pda_status true
                 :violation_55 0
                 :license_number 2300006
                 :violation_56 0
                 :violation_15 0
                 :violation_05 0
                 :violation_33 0
                 :violation_49 0
                 :violation_08 0
                 :violation_39 0
                 :location_zipcode "331321191"
                 :violation_45 0
                 :violation_34 0
                 :critical_violations_before_2013 nil
                 :violation_25 0
                 :violation_40 0
                 :violation_12 0
                 :high_priority_violations 1
                 :violation_10 0
                 :inspection_visit_id 5769863
                 :violation_31 0
                 :inspection_number 2498647
                 :basic_violations 1
                 :violation_35 0
                 :license_id "2153540"
                 :violation_32 0
                 :violation_28 0
                 :violation_13 0
                 :inspection_class "Food"
                 :total_violations 3
                 :violation_30 0
                 :violation_04 0
                 :inspection_disposition "Call Back - Complied"
                 :violation_53 0
                 :violation_29 1
                 :violation_24 0
                 :violation_43 0
                 :violation_46 0
                 :violation_19 0
                 :violation_41 0
                 :violation_03 0
                 :violation_18 0
                 :violation_52 0
                 :district "D1"
                 :intermediate_violations 1
                 :inspection_type "Routine - Food"
                 :violation_21 0
                 :created_on (util/todays-date)
                 :modified_on (util/todays-date)})

(deftest csv-row->map-test
  (testing "Converts csv raw row into a map"
    (is (= map-result (csv-row->map (first test-data))))))

(deftest violations-test
  (testing "retrieves all violations from map with value > 0"
    (is (= '([:violation_36 1] [:violation_27 1] [:violation_29 1])
           (violations map-result)))))
