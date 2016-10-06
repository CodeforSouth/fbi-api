(ns restaurant-inspections-api.routes
  (:require
   [compojure.route :refer [not-found]]
   [liberator.core :refer [resource defresource]]
   [ring.middleware.params :refer [wrap-params]]
   [compojure.core :refer [defroutes ANY GET]]
   ;; internal
   [restaurant-inspections-api.services :as srv]
   [restaurant-inspections-api.responses :as responses]
   [restaurant-inspections-api.util :as util]
   [restaurant-inspections-api.validations :as validate]
   [restaurant-inspections-api.handlers.inspections :as inspections]))


(defroutes all-routes

  (GET "/" [] (srv/home))

  (ANY "/counties" []
       (resource
        :allowed-methods [:get]
        :available-media-types ["application/json"]
        ;; :processable? inspections/inspections-processable?
        ;; :handle-unprocessable-entity #(get % :errors-map)
        ;; :handle-not-found	(fn [ctx] {:errors "No results found."})
        ;; :handle-malformed (400 BAD REQUEST)
        :handle-ok (fn [ctx] (srv/get-counties))))

  (ANY "/inspections/:id" [id]
       (resource
        :allowed-methods [:get]
        :available-media-types ["application/json"]
        ;; :processable? inspections/inspections-processable?
        ;; :handle-unprocessable-entity #(get % :errors-map)
        ;; :handle-not-found	(fn [ctx] {:errors "No results found."})
        ;; :handle-malformed (400 BAD REQUEST)
        :handle-ok (fn [ctx] (srv/full-inspection-details id))))

  (ANY "/inspections" [] ; query params available: see query-params-order vector
       (resource
        :allowed-methods [:get]
        :available-media-types ["application/json"]
        :processable? inspections/inspections-processable?
        :handle-unprocessable-entity #(get % :errors-map)
        ;; :handle-not-found (404 NOT FOUND)
        ;; :handle-malformed (400 BAD REQUEST)
        :handle-ok (fn [ctx]
                     ;;       (srv/inspections-by-zipcodes  zips startDate endDate)
                     ;;       (srv/inspections-by-business-name name startDate endDate))
                     ;;       (srv/inspections-by-district district-id startDate endDate)
                     ;;       (srv/inspections-by-county id startDate endDate))
                     {:meta {:parameters (get ctx :valid-params)}
                      :data (into [] (srv/inspections-by-all (get ctx :valid-params)))
                      }
                     )))

  ;; TODO: businesss
  (ANY "/businesses" []
       (resource
        :allowed-methods [:get]
        :available-media-types ["application/json"]
        :processable? #(-> %)
        :handle-unprocessable-entity #(get % :errors-map)
        :handle-ok (fn [ctx]
                     {:ok true})
        ))

  ;; TODO: violation codes/definitions
  (ANY "/violations" []
       (resource
        :allowed-methods [:get]
        :available-media-types ["application/json"]
        :processable? #(-> %)
        :handle-unprocessable-entity #(get % :errors-map)
        :handle-ok (fn [ctx]
                     {:ok true})
        ))

  ;; Default 404 when there's no match
  ;; TODO change body to something meaningful
  (ANY "*" []
       not-found))
