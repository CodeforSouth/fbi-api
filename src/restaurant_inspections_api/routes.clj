(ns restaurant-inspections-api.routes
  (:gen-class)
  (:require [restaurant-inspections-api.services :as srv]
            [compojure.core :refer [GET defroutes]]))

(defroutes all-routes
           (GET "/" [] (srv/home))
           (GET "/get/:id" [id] (srv/get-details id))
           (GET "/list-counties" [] (srv/get-dist-counties))
           (GET ["/location/:start-date/:end-date/:zips"
                 :zips #"[^/]+"]
                [start-date end-date zips]
                (srv/location start-date end-date zips))
           (GET ["/name/:start-date/:end-date/:name"
                 :name #"[^/]+"]
                [id start-date end-date name]
                (srv/get-name start-date end-date name))
           (GET ["/name/:start-date/:end-date/:name/:zips"
                 :name #"[^/]+"
                 :zips #"[^/]+"]
                [id start-date end-date name zips]
                (srv/get-name start-date end-date name zips))
           (GET "/district/:start-date/:end-date/:id"
                [start-date end-date id]
                (srv/district id start-date end-date))
           (GET "/county/:start-date/:end-date/:id"
                [start-date end-date id]
                (srv/county id start-date end-date)))

