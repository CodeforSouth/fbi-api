(ns restaurant-inspections-api.routes
  (:require [restaurant-inspections-api.services :as srv]
            [compojure.core :refer [GET defroutes]]))

(defroutes all-routes
           (GET "/" [] (srv/home))
           (GET "/inspections/:id" [id] (srv/get-details id))
           (GET "/counties" [] (srv/get-dist-counties))
           (GET ["/locations/:zips"]
                [startDate endDate zips]
                (srv/location startDate endDate zips))
           (GET ["/restaurants/:name"]
                [startDate endDate name]
                (srv/get-name startDate endDate name))
           (GET ["/businesses/:name/:zips"]
                [startDate endDate name zips]
                (srv/get-name startDate endDate name zips))
           (GET "/districts/:id"
                [startDate endDate id]
                (srv/district id startDate endDate))
           (GET "/counties/:id"
                [startDate endDate id]
                (srv/county id startDate endDate)))

