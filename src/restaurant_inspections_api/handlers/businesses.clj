(ns restaurant-inspections-api.handlers.businesses
  (:require [restaurant-inspections-api.validations :as validate]
            [restaurant-inspections-api.util :as util]
            [restaurant-inspections-api.services :as srv]))

(defn validate-businesses-params
  "Recieves all businesses query parameters (nil when not specified) and
  returns a map of valid and invalid params"
  [zip-codes county-number per-page page]

  (validate/validate-params {:zipCodes (validate/zip-codes zip-codes)
                             :county (validate/county-number county-number)
                             :perPage (validate/per-page per-page)
                             :page (validate/page page)}))

(defn processable?
  "Given a ring server ctx, returns an array with true or false if parameters
   are valid/processable and the errors or valid params"
  [ctx]
  (let [zip-codes (get-in ctx [:request :params :zipCodes])
        county-number (get-in ctx [:request :params :countyNumber])
        per-page (or (get-in ctx [:request :params :perPage]) "20")
        page (or (get-in ctx [:request :params :page]) "0")
        validations-map (validate-businesses-params zip-codes
                                                     county-number
                                                     per-page page)]
    (if-not (empty? (:invalid validations-map))
      [false {:errors-map
              {:errors (into [] (for [keyval (:invalid validations-map)]
                                  (util/format-query-params-error (name (key keyval)))))}
               :params (:valid validations-map)}]
      [true {:valid-params (:valid validations-map)}])))

(defn handle-ok
  "Handles 200 OK for inspections"
  [{:keys [valid-params]}]
  {:meta {:parameters valid-params}
   :data (into [] (srv/get-businesses valid-params))})

(defn handle-unprocessable
  "Handles 422 Unprocessable when invalid params provided"
  [ctx]
  (merge {:meta
          {:parameters (get ctx :params)}}
         (get ctx :errors-map)))
