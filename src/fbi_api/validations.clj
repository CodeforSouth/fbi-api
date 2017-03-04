(ns fbi-api.validations
  (:require [fbi-api.util :as util]))

(defn zip-codes
  "Validates zip codes. Returns original value if passes, nil if nil allowed,
  or false if not valid. Zip codes format: 33136,00976,33137."
  [zip-codes]
  (when-not (nil? zip-codes)
    (let [splitted-zip-codes-vector (clojure.string/split zip-codes #",")
          matches (map (partial re-matches #"\d{5}|\d{9}") splitted-zip-codes-vector)]
      ;; the whole list should match, so no nil elements should be present
      (and (not (boolean (some nil? matches))) zip-codes))))

;; TODO: Make regular expression better, to make conditional on 10 or 01 dates
(defn date
  "Validates dates. Returns original value if passes, nil if nil allowed,
  or false if not valid. Date format: YYYY-MM-DD."
  [date]
  (when-not (nil? date)
    (and (boolean (re-matches #"[1-2]\d{3}-(0|1)[0-9]-[0-3][0-9]" date)) date)))

(defn district-code
  "Validates a district code. Returns original value if passes, nil if nil is allowed,
  or false if not valid. District code format: D11."
  [district-code]
  (when-not (nil? district-code)
    (and (boolean (re-matches #"D[0-9]{1,2}" district-code)) district-code)))

(defn county-number
  "Validates a county code. Returns original value if passes, nil if nil is allowed,
  or false if not valid. County number format: any integer."
  [county-number]
  (when-not (nil? county-number)
    (and (boolean (re-matches #"\d{1,3}" county-number)) county-number)))

(defn per-page
  "Validates that the provided per-page param is a number or nil"
  [per-page]
  (when-not (nil? per-page)
    (and (boolean (re-matches #"\d+" per-page)) (Integer. per-page))))

(defn page
  "Validate that the provided page (offset) param is a number or nil"
  [page]
  (when-not (nil? page)
    (and (boolean (re-matches #"\d+" page)) (Integer. page))))

(defn validate-params
  "Receives a dictionary with parameter names and boolean values
   and returns two arrays of valid and invalid params"
  [validated-map]
  {:invalid (into {} (filter #(false? (second %)) validated-map))
   :valid (into {} (filter #(not (false? (second %)))) validated-map)})

(defn processable?
  "Given a ring server ctx, returns an array with true or false if parameters
   are valid/processable and the errors or valid params"
  [validatefn ctx]
  (let [params (get-in ctx [:request :params])
        validations-map (validatefn (into {} (filter (comp some? val) params)))]
    (if-not (empty? (:invalid validations-map))
      [false {:errors-map
              {:errors (into [] (for [keyval (:invalid validations-map)]
                                  (util/format-query-params-error (name (key keyval)))))}
              :params (:valid validations-map)}]
      [true {:valid-params (:valid validations-map)}])))
