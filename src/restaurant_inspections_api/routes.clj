(ns restaurant-inspections-api.routes
  (:require
   [liberator.core :refer [resource defresource]]
   [compojure.core :refer [defroutes ANY GET]]
   [compojure.route :refer [not-found]]
   ;; internal
   [restaurant-inspections-api.services :as srv]
   [restaurant-inspections-api.util :as util]
   [restaurant-inspections-api.validations :as validate]
   [restaurant-inspections-api.handlers.inspections :as inspections]))

(defroutes all-routes

  (GET "/" [] {:status 302
               :headers {"Location" "https://github.com/Code-for-Miami/restaurant-inspections-api/wiki"}})

  (ANY "/counties" []
    (resource
     :allowed-methods [:get]
     :available-media-types ["application/json"]
     ;; :handle-not-found	(fn [ctx] {:errors "No results found."})
     ;; TODO: malformed when receiving query params
     ;; :handle-malformed (400 BAD REQUEST)
     :handle-ok (fn [ctx] (srv/get-counties))))

  (ANY "/inspections/:id" [id] ;; id = inspections_visit_id
    (resource
     :allowed-methods [:get]
     :available-media-types ["application/json"]
     ;; :processable? inspections/inspections-processable?
     ;; :handle-unprocessable-entity #(get % :errors-map)
     ;; TODO: Not found when inspection id is not found (instead of 200)
     ;; :handle-not-found	(fn [ctx] {:errors "No results found."})
     ;; TODO: malformed when receiving query params
     ;; :handle-malformed (400 BAD REQUEST)
     :handle-ok (fn [ctx] (srv/full-inspection-details id))))

  (ANY "/inspections" []
    (resource
     :allowed-methods [:get]
     :available-media-types ["application/json"]
     :processable? inspections/processable?
     :handle-unprocessable-entity #(get % :errors-map)
     ;; :handle-not-found (404 NOT FOUND)
     ;; TODO: handle malformed when passing unknown query params
     ;; :handle-malformed (400 BAD REQUEST)
     ;; TODO: return count of returned data elements
     :handle-ok inspections/handle-ok))

  ;; TODO: Better api handling of businesses
  (ANY "/businesses" []
    (resource
     :allowed-methods [:get]
     :available-media-types ["application/json"]
     ;; TODO: handle query params, the same way inspections do
     ;; :processable? #(-> %)
     ;;:handle-unprocessable-entity #(get % :errors-map)
     :handle-ok (fn [ctx]
                  (srv/get-businesses))))

  ;; TODO: Better api handling of violation codes/definitions
  (ANY "/violations" []
    (resource
     :allowed-methods [:get]
     :available-media-types ["application/json"]
     :processable? #(-> %)
     :handle-unprocessable-entity #(get % :errors-map)
     :handle-ok (fn [ctx]
                  (srv/get-violations))))

  ;; Default 404 when there's no match
  ;; TODO change body to something meaningful
  (not-found "404 NOT FOUND"))
