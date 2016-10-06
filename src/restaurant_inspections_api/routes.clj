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
   [restaurant-inspections-api.util :as util]
   [restaurant-inspections-api.validations :as validate]
   [restaurant-inspections-api.handlers.inspections :as inspections]))


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
        :processable? inspections/inspections-processable?
        :handle-unprocessable-entity (fn [ctx] (get ctx :error-map))
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
                     {:result (get ctx :valid-params)}
                     )))

  ;; Default 404 when there's no match
  ;; TODO change body to something meaningful
  (ANY "*" []
       (not-found {:error true})))
