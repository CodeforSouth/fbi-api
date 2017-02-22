(ns restaurant-inspections-api.routes
  (:require
   [liberator.core :refer [resource]]
   [compojure.core :refer [defroutes ANY GET]]
   [compojure.route :refer [not-found]]
   [taoensso.timbre :as log]
   ;; internal
   [restaurant-inspections-api.services :as srv]
   [restaurant-inspections-api.util :as util]
   [restaurant-inspections-api.validations :as validate]
   [restaurant-inspections-api.handlers.businesses :as businesses]
   [restaurant-inspections-api.handlers.inspections :as inspections]))

;; all routes return app/json;charset=UTF-8 headers (thanks to liberator/compojure)
(defroutes routes

  (ANY "/" []
    (resource
     :allowed-methods [:get]
     :available-media-types ["application/json"]
     :handle-ok (fn [ctx] {:api-name "FRIA: Florida's Restaurant Inspections API"
                           :description "Florida's Restaurant inspections are available in csv files. Also available on an old http form on their website."
                           :routes ["/" "/wiki" "/api-docs" "/counties" "/inspections" "/inspections/:id"
                                    "/businesses" "/businesses/:licenseNumber" "/violations"]})))

  (GET "/wiki" []
    {:status 302
     :headers {"Location" "https://github.com/Code-for-Miami/restaurant-inspections-api/wiki"}})

  ;; TODO: use id to check for county info
  (ANY "/counties" []
    (resource
     :allowed-methods [:get]
     :available-media-types ["application/json"]
     :handle-ok (fn [ctx] {:meta {}
                           :data (srv/get-counties)})))

  (ANY "/inspections" []
    (resource
     :allowed-methods [:get]
     :available-media-types ["application/json"]
     :processable? inspections/processable?
     :handle-unprocessable-entity inspections/handle-unprocessable
     :handle-ok inspections/handle-ok))

  ;; TODO: return different status code and error? in body if no inspection with provided id
  (ANY "/inspections/:id" [id]
    (resource
     :allowed-methods [:get]
     :available-media-types ["application/json"]
     :handle-ok (fn [ctx] {:meta {}
                           :data (let [inspection (srv/full-inspection-details id)]
                                   (if inspection
                                     [inspection]
                                     []))})))

 (ANY "/businesses" []
  (resource
   :allowed-methods [:get]
   :available-media-types ["application/json"]
   :processable? businesses/processable?
   :handle-unprocessable-entity businesses/handle-unprocessable
   :handle-ok businesses/handle-ok))

  (ANY "/businesses/:licenseNumber" [licenseNumber]
    (resource
     :allowed-methods [:get]
     :available-media-types ["application/json"]
     :handle-ok (fn [ctx] {:meta {}
                           :data (let [business (srv/full-business-details licenseNumber)]
                                   (if business
                                     [business]
                                     []))})))

  (ANY "/violations" []
    (resource
     :allowed-methods [:get]
     :available-media-types ["application/json"]
     :processable? #(-> %)
     :handle-unprocessable-entity #(get % :errors-map)
     :handle-ok (fn [ctx]
                  {:meta {}
                   :data (srv/get-violations)})))

  (not-found "404 NOT FOUND"))

