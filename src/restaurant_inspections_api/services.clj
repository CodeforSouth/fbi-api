(ns restaurant-inspections-api.services
  (:require [restaurant-inspections-api.responses :as res]
            [clj-time.format :as f]
            [clj-time.coerce :as c]
            [restaurant-inspections-api.environment :as env]
            [clojure.java.jdbc :as db]
            [clojure.string :as str]))

(def db-url (env/get-env-db-url))

(defn home
  "go to project wiki"
  []
  (res/redirect "https://github.com/Code-for-Miami/restaurant-inspections-api/wiki"))

(defn basic-fields-sql
  "return a string with the basic inspection fields"
  []
  (str "inspection_visit_id, district, county_number, county_name, license_type_code, license_number, "
       "business_name, inspection_date, location_address, location_city, location_zipcode, "
       "inspection_number, visit_number, inspection_type, inspection_disposition, "
       "total_violations, high_priority_violations, "
       "intermediate_violations, basic_violations"))

(defn format-data
  "formats db raw data to json pattern"
  ([data]
   (format-data data false))
  ([data is-full]
    (let [basic-data {:id                              (:inspection_visit_id data)
                      :district                        (:district data)
                      :countyNumber                    (:county_number data)
                      :countyName                      (:county_name data)
                      :licenseTypeCode                 (:license_type_code data)
                      :licenseNumber                   (:license_number data)
                      :businessName                    (:business_name data)
                      :inspectionDate                  (f/unparse (f/formatter "YYYY-MM-dd")
                                                                  (c/from-date (:inspection_date data)))
                      :locationAddress                 (:location_address data)
                      :locationCity                    (:location_city data)
                      :locationZipcode                 (:location_zipcode data)
                      :inspectionNumber                (:inspection_number data)
                      :visitNumber                     (:visit_number data)
                      :inspectionType                  (:inspection_type data)
                      :inspectionDisposition           (:inspection_disposition data)
                      :totalViolations                 (:total_violations data)
                      :highPriorityViolations          (:high_priority_violations data)
                      :intermediateViolations          (:intermediate_violations data)
                      :basicViolations                 (:basic_violations data)}]
      (if is-full
        (into basic-data
              {:criticalViolationsBefore2013      (:critical_violations_before_2013 data)
               :nonCriticalViolationsBefore2013   (:noncritical_violations_before_2013 data)
               :pdaStatus                         (:pda_status data)
               :licenseId                         (:license_id data)
               :violations {:violation01          (:violation_01 data)
                            :violation02          (:violation_02 data)
                            :violation03          (:violation_03 data)
                            :violation04          (:violation_04 data)
                            :violation05          (:violation_05 data)
                            :violation06          (:violation_06 data)
                            :violation07          (:violation_07 data)
                            :violation08          (:violation_08 data)
                            :violation09          (:violation_09 data)
                            :violation10          (:violation_10 data)
                            :violation11          (:violation_11 data)
                            :violation12          (:violation_12 data)
                            :violation13          (:violation_13 data)
                            :violation14          (:violation_14 data)
                            :violation15          (:violation_15 data)
                            :violation16          (:violation_16 data)
                            :violation17          (:violation_17 data)
                            :violation18          (:violation_18 data)
                            :violation19          (:violation_19 data)
                            :violation20          (:violation_20 data)
                            :violation21          (:violation_21 data)
                            :violation22          (:violation_22 data)
                            :violation23          (:violation_23 data)
                            :violation24          (:violation_24 data)
                            :violation25          (:violation_25 data)
                            :violation26          (:violation_26 data)
                            :violation27          (:violation_27 data)
                            :violation28          (:violation_28 data)
                            :violation29          (:violation_29 data)
                            :violation30          (:violation_30 data)
                            :violation31          (:violation_31 data)
                            :violation32          (:violation_32 data)
                            :violation33          (:violation_33 data)
                            :violation34          (:violation_34 data)
                            :violation35          (:violation_35 data)
                            :violation36          (:violation_36 data)
                            :violation37          (:violation_37 data)
                            :violation38          (:violation_38 data)
                            :violation39          (:violation_39 data)
                            :violation40          (:violation_40 data)
                            :violation41          (:violation_41 data)
                            :violation42          (:violation_42 data)
                            :violation43          (:violation_43 data)
                            :violation44          (:violation_44 data)
                            :violation45          (:violation_45 data)
                            :violation46          (:violation_46 data)
                            :violation47          (:violation_47 data)
                            :violation48          (:violation_48 data)
                            :violation49          (:violation_49 data)
                            :violation50          (:violation_50 data)
                            :violation51          (:violation_51 data)
                            :violation52          (:violation_52 data)
                            :violation53          (:violation_53 data)
                            :violation54          (:violation_54 data)
                            :violation55          (:violation_55 data)
                            :violation56          (:violation_56 data)
                            :violation57          (:violation_57 data)
                            :violation58          (:violation_58 data)}})
        basic-data))))

(defn location
  "return inspections per given location and period"
  [start-date end-date zips]
  (let [zips (str/split zips #",")]
    (res/ok (map format-data
                 (db/query db-url
                           (concat [(str "SELECT " (basic-fields-sql)
                                       "  FROM restaurant_inspections"
                                       " WHERE inspection_date BETWEEN ? AND ?"
                                       "   AND location_zipcode IN ("
                                       (str/join ", " (take (count zips) (repeat "?")))
                                       ") LIMIT 1000") start-date end-date] zips))))))

(def name-basic-sql
  (str "SELECT " (basic-fields-sql)
       "  FROM restaurant_inspections"
       " WHERE inspection_date BETWEEN ? AND ?"
       "   AND business_name LIKE ? "))

(defn get-name
  "return inspections per given business name, location and period"
  ([start-date end-date name]
   (res/ok (map format-data
                (db/query db-url
                          [(str name-basic-sql
                                " LIMIT 1000") start-date end-date
                           (str/replace name #"\*" "%")]))))
  ([start-date end-date name zips]
   (let [zips (str/split zips #",")]
     (res/ok (map format-data
                  (db/query db-url
                            (concat [(str name-basic-sql
                                          " AND location_zipcode IN ("
                                          (str/join ", " (take (count zips) (repeat "?")))
                                          ") LIMIT 1000") start-date end-date
                                     (str/replace name #"\*" "%")] zips)))))))

(defn district
  "return inspections per given district and period"
  [id start-date end-date]
  (res/ok (map format-data
               (db/query db-url
                         [(str "SELECT " (basic-fields-sql)
                                     "  FROM restaurant_inspections"
                                     " WHERE inspection_date BETWEEN ? AND ?"
                                     "   AND district = ?"
                                     " LIMIT 1000") start-date end-date id] ))))

(defn county
  "return inspections per given county and period"
  [id start-date end-date]
  (res/ok (map format-data
               (db/query db-url
                         [(str "SELECT " (basic-fields-sql)
                                     "  FROM restaurant_inspections"
                                     " WHERE inspection_date BETWEEN ? AND ?"
                                     "   AND county_number = ?"
                                     " LIMIT 1000") start-date end-date id] ))))

(defn get-details
  "return full info for the given Id"
  [id]
  (res/ok (format-data (first (db/query db-url
                                        ["SELECT *
                             FROM restaurant_inspections
                            WHERE inspection_visit_id = ?" id]))
                       true)))

(defn get-dist-counties
  "return district and counties list"
  []
  (res/ok (db/query db-url
                    [(str "SELECT district, county_number as countyNumber, "
                          "       county_name as countyName, count(*) as inspections "
                          "  FROM restaurant_inspections "
                          " GROUP BY district, county_number, county_name"
                          " ORDER by district, county_name")])))