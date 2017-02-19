(ns restaurant-inspections-api.routes
  (:require
   [liberator.core :refer [resource defresource]]
   [compojure.core :refer [defroutes ANY GET]]
   [compojure.route :refer [not-found]]
   ;; internal
   [restaurant-inspections-api.services :as srv]
   [restaurant-inspections-api.util :as util]
   [taoensso.timbre :refer [debug]]
   [restaurant-inspections-api.validations :as validate]
   [restaurant-inspections-api.handlers.inspections :as inspections]))

;; all routes return app/json;charset=UTF-8 headers (thanks to liberator/compojure)
(defroutes all-routes

  (ANY "/" []
    (resource
     :allowed-methods [:get]
     :available-media-types ["application/json"]
     :handle-ok (fn [ctx] {:api-name "FRIA: Florida's Restaurant Inspections API"
                           :description "Florida's Restaurant inspections are available in csv files. Also available on an old http form on their website."
                           :routes ["/" "/wiki" "/counties" "/inspections" "/inspections/:id"
                                    "/businesses" "/violations"]})))

  (GET "/wiki" []
    {:status 302
     :headers {"Location" "https://github.com/Code-for-Miami/restaurant-inspections-api/wiki"}})

  (ANY "/counties" []
    (resource
     :allowed-methods [:get]
     :available-media-types ["application/json"]
     :handle-ok (fn [ctx] {:meta {}
                           :data (srv/get-counties)})))

  (ANY "/inspections/:id" [id]
    (resource
     :allowed-methods [:get]
     :available-media-types ["application/json"]
     :handle-ok (fn [ctx] {:meta {}
                           :data (let [inspection (srv/full-inspection-details id)]
                                   (if inspection
                                     [inspection]
                                     []))})))

  (ANY "/inspections" []
    (resource
     :allowed-methods [:get]
     :available-media-types ["application/json"]
     :processable? inspections/processable?
     :handle-unprocessable-entity inspections/handle-unprocessable
     :handle-ok inspections/handle-ok))

  (ANY "/businesses/:id" [id]
    (resource
     :allowed-methods [:get]
     :available-media-types ["application/json"]
     :handle-ok (fn [ctx] {:meta {}
                           :data (let [business (srv/full-business-details id)]
                                   (if business
                                     [business]
                                     []))})))

  ;; TODO: Better api handling of businesses
  (ANY "/businesses" []
    (resource
     :allowed-methods [:get]
     :available-media-types ["application/json"]
     ;; TODO: handle query params, the same way inspections do
     :processable? (fn [ctx]
                     (let [per-page (or (get-in ctx [:request :params :perPage]) "20")
                           page (or (get-in ctx [:request :params :page]) "0")]
                       [true {:valid-params {:perPage (validate/per-page per-page) :page (validate/page page)}}]))
     ;;:handle-unprocessable-entity #(get % :errors-map)
     :handle-ok (fn [{:keys [valid-params] :as ctx}]
                  {:meta {:parameters valid-params}
                   :data (srv/get-businesses valid-params)})))

  ;; TODO: Better api handling of violation codes/definitions
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

