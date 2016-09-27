(ns restaurant-inspections-api.routes
  (:require [restaurant-inspections-api.services :as srv]
            [compojure.core :refer [GET defroutes]]))

(defroutes all-routes
           (GET "/" [] (srv/home))
           (GET "/inspections/:id" [id] (srv/inspection id))
           (GET "/counties" [] (srv/get-dist-counties))
           (GET ["/locations/:zips"]
                [zips startDate endDate]
                (srv/location zips startDate endDate))
           (GET ["/businesses/:name"]
                [name zips startDate endDate]
                (srv/business name zips startDate endDate))
           (GET "/districts/:id"
                [id startDate endDate]
                (srv/district id startDate endDate))
           (GET "/counties/:id"
                [id startDate endDate]
                (srv/county id startDate endDate)))

