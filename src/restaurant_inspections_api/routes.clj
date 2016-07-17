(ns restaurant-inspections-api.routes
  (:gen-class)
  (:require [restaurant-inspections-api.services :as srv]
            [compojure.core :refer [GET defroutes]]))

(defroutes all-routes
           (GET "/" [] (srv/home))
           (GET "/top10" [] (srv/top10))
           (GET "/get/:id" [id] (srv/get-details id))
           (GET ["/location/:start-date/:end-date/:zips"
                 :zips #"[^/]+"]
                [start-date end-date zips]
                (srv/location start-date end-date zips))
           (GET "/business/:start-date/:end-date/:name/:zips"
                [id start-date end-date]
                (srv/get-details id))
           (GET "/district/:id/:start-date/:end-date"
                [id start-date end-date]
                (srv/district id start-date end-date))
           (GET "/county/:id/:start-date/:end-date"
                [id start-date end-date]
                (srv/county id start-date end-date)))

