(ns fbi-api.routes
  (:require
   [liberator.core :refer [resource]]
   [compojure.core :refer [defroutes ANY GET]]
   [compojure.route :refer [not-found]]
   [taoensso.timbre :as log]
   ;; internal
   [fbi-api.db :as db]
   [fbi-api.util :as util]
   [fbi-api.validations :as validate]
   [fbi-api.handlers.businesses :as businesses]
   [fbi-api.handlers.inspections :as inspections]
   [fbi-api.handlers.counties :as counties]))

;; all routes return app/json;charset=UTF-8 headers (thanks to liberator/compojure)
(defroutes routes

  (ANY "/" []
    (resource
     :allowed-methods [:get]
     :available-media-types ["application/json"]
     :handle-ok (fn [ctx] {:api-name "FBI: Florida's Business Inspections API"
                           :description "Florida Business Inspections are available in csv files. Also available on an http form on dbpr website."
                           :routes ["/"
                                    "/wiki"
                                    "/api-docs"
                                    "/counties"
                                    "/inspections"
                                    "/inspections/:id"
                                    "/businesses"
                                    "/businesses/:licenseNumber"
                                    "/violations"]})))

  (GET "/wiki" []
    {:status 302
     :headers {"Location" "https://github.com/Code-for-Miami/fbi-api/wiki"}})

  (ANY "/counties" []
    (resource
     :allowed-methods [:get]
     :available-media-types ["application/json"]
     :handle-ok counties/handle-ok))

  (ANY "/inspections" []
    (resource
     :allowed-methods [:get]
     :available-media-types ["application/json"]
     :processable? inspections/processable?
     :handle-unprocessable-entity inspections/handle-unprocessable
     :handle-ok inspections/handle-ok))

  ;; TODO: return different status code and different body if no inspection with provided id?
  (ANY "/inspections/:id" [id]
    (resource
     :allowed-methods [:get]
     :available-media-types ["application/json"]
     :handle-ok (fn [ctx] (inspections/handle-individual-ok id))))

  (ANY "/businesses" []
    (resource
     :allowed-methods [:get]
     :available-media-types ["application/json"]
     :processable? businesses/processable?
     :handle-unprocessable-entity businesses/handle-unprocessable
     :handle-ok businesses/handle-ok))

  (ANY "/businesses/:id" [id]
    (resource
     :allowed-methods [:get]
     :available-media-types ["application/json"]
     :handle-ok (fn [ctx] (businesses/handle-individual-ok id))))

  (ANY "/violations" []
    (resource
     :allowed-methods [:get]
     :available-media-types ["application/json"]
     :processable? #(-> %)
     :handle-unprocessable-entity #(get % :errors-map)
     :handle-ok (fn [ctx]
                  {:meta {}
                   :data (db/select-all-violations)})))

  (not-found "404 NOT FOUND"))
