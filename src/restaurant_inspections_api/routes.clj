(ns restaurant-inspections-api.routes
    (:require
        [compojure.core :refer [GET defroutes]]
        [compojure.route :refer [not-found]]
        [liberator.core :refer [resource defresource]]
        [ring.middleware.params :refer [wrap-params]]
        [compojure.core :refer [defroutes ANY]]
        ; internal
        [restaurant-inspections-api.services :as srv]
        [restaurant-inspections-api.responses :as responses]
        [restaurant-inspections-api.util :as util]))

(defn validate-zip-codes
    [zipCodes]
    (if (nil? zipCodes)
        nil
        (let [splitted-zip-codes-vector (clojure.string/split zipCodes #",")
              matches (map (partial re-matches #"\d{5}") splitted-zip-codes-vector)]
            ;; the whole list should match, so no nil elements should be present
            (not (boolean (some nil? matches))))))

(defn validate-date
    [date]
    (boolean (re-matches #"[1-2]\d{3}-(0|1)[0-9]-[0-3][1-9]" date)))

(defn validate-district-code
    [district-code]
    (if (nil? district-code)
        nil
        (boolean (re-matches #"D[0-9]{1,2}" district-code))))

(defn validate-county-number
    [county-number]
    (if (nil? county-number)
        nil
        (boolean (re-matches #"\d{1,3}" county-number))))

(def query-params-order
    ["zipCodes", "businessName", "startDate", "endDate", "districtCode", "countyNumber"])

(def validate-inspections-request
    (fn [ctx]
        (let [zip-codes (get-in ctx [:request :params :zipCodes])
              business-name (get-in ctx [:request :params :businessName])
              start-date (or (get-in ctx [:request :params :startDate]) "2013-01-01")
              end-date (or (get-in ctx [:request :params :endDate]) (util/todays-date))
              district-code (get-in ctx [:request :params :districtCode])
              county-number (get-in ctx [:request :params :countyNumber])

              valid-params [(validate-zip-codes zip-codes)
                            (if (nil? business-name) nil true)
                            (validate-date start-date)
                            (validate-date end-date)
                            (validate-district-code district-code)
                            (validate-county-number county-number)]

              invalid-param-index (.indexOf valid-params false)]

            (if (not= invalid-param-index -1)
                [false {:error {:error (str "Invalid format or value for parameter " (nth query-params-order invalid-param-index))}}]
                [true {:validation-vector valid-params}]))))

(defn handle-inspections-ok
  ""
  [ctx]
  true)

(defroutes all-routes

    ;; working:
    (GET "/" [] (srv/home))

    (GET "/inspections/:id" [id] (srv/full-inspection-details id))

    (GET "/counties" [] (srv/get-counties))

    ;; not working:

    (ANY "/inspections" [] ; query params available: see query-params-order vector
         (resource
             :allowed-methods [:get]
             :available-media-types ["application/json"]
             :processable? validate-inspections-request
             :handle-unprocessable-entity (fn [ctx] (get ctx :error))
             :handle-not-found	#(-> {:error "No results found."})
             :handle-method-not-allowed #(-> {:error "Method not allowed on this resource."})
;;           :handle-malformed
             :handle-ok (fn [ctx]
                            ;;                 (case valid-params
                            ;;          (srv/inspections-by-zipcodes  zips startDate endDate)
                            ;;          (srv/inspections-by-business-name name startDate endDate))
                            ;;          (srv/inspections-by-business-name name zips startDate endDate))
                            ;;          (srv/inspections-by-district district-id startDate endDate))
                            ;;          (srv/inspections-by-county id startDate endDate))
                            ;; [true true true true true true] {:num 0}
                            ;; [nil true true true true true] {:num 2}
                            ;;                 :else {:num 1}
                            ;;                     ))
                            {:result (get ctx :validation-vector)}
                            )))

    ;; Default 404 when there's no match
    ;; TODO change body to something meaningful
    (ANY "*" []
         (not-found {:error true})))
