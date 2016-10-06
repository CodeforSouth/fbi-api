(ns restaurant-inspections-api.validations)

(defn zip-codes
  "Validates zip codes. Returns original value if passes, nil if nil allowed,
  or false if not valid. Zip codes format: 33136,00976,33137."
  [zip-codes]
  (if (nil? zip-codes)
    nil
    (let [splitted-zip-codes-vector (clojure.string/split zip-codes #",")
          matches (map (partial re-matches #"\d{5}") splitted-zip-codes-vector)]
      ;; the whole list should match, so no nil elements should be present
      (and (not (boolean (some nil? matches))) zip-codes))))

(defn date
  "Validates dates. Returns original value if passes, nil if nil allowed,
  or false if not valid. Date format: YYYY-MM-DD."
  [date]
  (and (boolean (re-matches #"[1-2]\d{3}-(0|1)[0-9]-[0-3][1-9]" date)) date))

(defn district-code
  "Validates a district code. Returns original value if passes, nil if nil is allowed,
  or false if not valid. District code format: D11."
  [district-code]
  (if (nil? district-code)
    nil
    (and (boolean (re-matches #"D[0-9]{1,2}" district-code)) district-code)))

(defn county-number
  "Validates a county code. Returns original value if passes, nil if nil is allowed,
  or false if not valid. County number format: any integer."
  [county-number]
  (if (nil? county-number)
    nil
    (and (boolean (re-matches #"\d{1,3}" county-number)) county-number)))

