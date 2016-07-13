(ns restaurant-inspections-api.services
  (:require [restaurant-inspections-api.responses :as res]
            [clj-time.format :as f]
            [clj-time.coerce :as c]
            [restaurant-inspections-api.environment :as env]
            [clojure.java.jdbc :as db]))

(def db-url (env/get-env-db-url))

(defn home
  "go to project wiki"
  []
  (res/redirect "https://github.com/Code-for-Miami/restaurant-inspections-api/wiki"))

(defn format-data
  "formats db raw data to json pattern"
  [data]
  {:id                              (:id data)
   :district                        (:district data)
   :countyNumber                    (:county_number data)
   :countyName                      (:county_name data)
   :licenseTypeCode                 (:license_type_code data)
   :licenseNumber                   (:license_number data)
   :businessName                    (:business_name data)
   :inspectionDate                  (f/unparse (f/formatter "YYYY-MM-dd")
                                               (c/from-date (:inspection_date data)))
   :inspectionNumber                (:inspection_number data)
   :visitNumber                     (:visit_number data)
   :inspectionClass                 (:inspection_class data)
   :inspectionType                  (:inspection_type data)
   :inspectionDisposition           (:inspection_disposition data)
   :inspectionVisitId               (:inspection_visit_id data)
   :licenseId                       (:license_id data)
   :pda_status                      (:pda_status data)
   :locationAddress                 (:location_address data)
   :locationCity                    (:location_city data)
   :locationZipcode                 (:location_zipcode data)
   :totalViolations                 (:total_violations data)
   :highPriorityViolations          (:high_priority_violations data)
   :intermediateViolations          (:intermediate_violations data)
   :basicViolations                 (:basic_violations data)
   :violation01                     (:violation_01 data)
   :violation02                     (:violation_02 data)
   :violation03                     (:violation_03 data)
   :violation04                     (:violation_04 data)
   :violation05                     (:violation_05 data)
   :violation06                     (:violation_06 data)
   :violation07                     (:violation_07 data)
   :violation08                     (:violation_08 data)
   :violation09                     (:violation_09 data)
   :violation10                     (:violation_10 data)
   :violation11                     (:violation_11 data)
   :violation12                     (:violation_12 data)
   :violation13                     (:violation_13 data)
   :violation14                     (:violation_14 data)
   :violation15                     (:violation_15 data)
   :violation16                     (:violation_16 data)
   :violation17                     (:violation_17 data)
   :violation18                     (:violation_18 data)
   :violation19                     (:violation_19 data)
   :violation20                     (:violation_20 data)
   :violation21                     (:violation_21 data)
   :violation22                     (:violation_22 data)
   :violation23                     (:violation_23 data)
   :violation24                     (:violation_24 data)
   :violation25                     (:violation_25 data)
   :violation26                     (:violation_26 data)
   :violation27                     (:violation_27 data)
   :violation28                     (:violation_28 data)
   :violation29                     (:violation_29 data)
   :violation30                     (:violation_30 data)
   :violation31                     (:violation_31 data)
   :violation32                     (:violation_32 data)
   :violation33                     (:violation_33 data)
   :violation34                     (:violation_34 data)
   :violation35                     (:violation_35 data)
   :violation36                     (:violation_36 data)
   :violation37                     (:violation_37 data)
   :violation38                     (:violation_38 data)
   :violation39                     (:violation_39 data)
   :violation40                     (:violation_40 data)
   :violation41                     (:violation_41 data)
   :violation42                     (:violation_42 data)
   :violation43                     (:violation_43 data)
   :violation44                     (:violation_44 data)
   :violation45                     (:violation_45 data)
   :violation46                     (:violation_46 data)
   :violation47                     (:violation_47 data)
   :violation48                     (:violation_48 data)
   :violation49                     (:violation_49 data)
   :violation50                     (:violation_50 data)
   :violation51                     (:violation_51 data)
   :violation52                     (:violation_52 data)
   :violation53                     (:violation_53 data)
   :violation54                     (:violation_54 data)
   :violation55                     (:violation_55 data)
   :violation56                     (:violation_56 data)
   :violation57                     (:violation_57 data)
   :violation58                     (:violation_58 data)
   :criticalViolationsBefore2013    (:critical_violations_before_2013 data)
   :noncriticalViolationsBefore2013 (:noncritical_violations_before_2013 data)})

(defn top10
  "return the top 10 worse (most violations) inspections"
  []
  (res/ok (map format-data
               (db/query db-url
                         ["SELECT *
                             FROM restaurant_inspections
                            ORDER BY total_violations DESC
                            LIMIT 10"]))))

(defn get-details
  "return full info for the given Id"
  [id]
  (res/ok (map format-data
               (db/query db-url
                         ["SELECT *
                             FROM restaurant_inspections
                            WHERE id = ?" id]))))