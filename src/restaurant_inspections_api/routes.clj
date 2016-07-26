(ns restaurant-inspections-api.routes
  (:gen-class)
  (:require [restaurant-inspections-api.services :as srv]
            [compojure.core :refer [GET defroutes]]))

(defroutes all-routes
           (GET "/" [] (srv/home))
           (GET "/inspection/:id" [id] (srv/get-details id))
           (GET "/list-counties" [] (srv/get-dist-counties))
           (GET ["/location/:zips" :zips #"[^/]+"]
                [startDate endDate zips]
                (srv/location startDate endDate zips))
           (GET ["/name/:name"
                 :name #"[^/]+"]
                [startDate endDate name]
                (srv/get-name startDate endDate name))
           (GET ["/name/:name/:zips"
                 :name #"[^/]+"
                 :zips #"[^/]+"]
                [startDate endDate name zips]
                (srv/get-name startDate endDate name zips))
           (GET "/district/:id"
                [startDate endDate id]
                (srv/district id startDate endDate))
           (GET "/county/:id"
                [startDate endDate id]
                (srv/county id startDate endDate)))

