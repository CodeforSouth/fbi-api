(ns restaurant-inspections-api.routes
    (:require [restaurant-inspections-api.services :as srv]
              [compojure.core :refer [GET defroutes]]
              [compojure.route :refer [not-found]]
              [liberator.core :refer [resource defresource]]
              [ring.middleware.params :refer [wrap-params]]
              [compojure.core :refer [defroutes ANY]]))

(defroutes all-routes

    (GET "/" [] (srv/home))

    (GET "/inspection/:id" [id] (srv/full-inspection-details id))

    (GET "/list-counties" [] (srv/get-counties))

    (GET ["/location/:zips"]
         [zips startDate endDate]
         (srv/inspections-by-zipcodes  zips startDate endDate))

    (GET ["/name/:name"]
         [name startDate endDate]
         (srv/inspections-by-business-name name startDate endDate))

    (GET ["/name/:name/:zips"]
         [name zips startDate endDate]
         (srv/inspections-by-business-name name zips startDate endDate))

    (GET "/district/:id"
         [id startDate endDate]
         (srv/inspections-by-district id startDate endDate))

    (GET "/county/:id"
         [id startDate endDate]
         (srv/inspections-by-county id startDate endDate))

    (ANY "/greet" []
         (resource
             :available-media-types ["application/json"]
             :handle-ok (fn [ctx] (str "Hello liberator"))))

    ;; Default 404 when there's no match
    ;; TODO change body to something meaningful
    (ANY "*" []
         (not-found "hello")))
