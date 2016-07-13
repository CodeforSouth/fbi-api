(ns restaurant-inspections-api.routes
  (:gen-class)
  (:require [restaurant-inspections-api.services :as srv]
            [compojure.core :refer [GET defroutes]]))

(defroutes all-routes
           (GET "/" [] (srv/home))
           (GET "/top10" [] (srv/top10))
           (GET "/get/:id" [id] (srv/get-details id))
           (GET "/business/:name/:zip/:date" [id date] (srv/get-details id))
           (GET "/business/:name/:zip/:start-date/:end-date" [id start-date end-date] (srv/get-details id))
           (GET "/district/:id/:date" [id date] (srv/get-details id))
           (GET "/district/:id/:start-date/:end-date" [id start-date end-date] (srv/get-details id))
           (GET "/county/:id/:date" [id date] (srv/get-details id))
           (GET "/county/:id/:start-date/:end-date" [id start-date end-date] (srv/get-details id)))

