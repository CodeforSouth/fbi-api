(ns restaurant-inspections-api.routes
  (:gen-class)
  (:require [restaurant-inspections-api.environment :as env]
            [restaurant-inspections-api.services :as srv]
            [org.httpkit.server :refer [run-server]]
            [ring.middleware.reload :as reload]
            [compojure.handler :refer [site]]
            [compojure.core :refer [GET defroutes]]))

(defroutes all-routes
           (GET "/" [] (srv/home))
           (GET "/top10" [] (srv/top10)))

(def port (env/get-env-port))
(defn -main
  "Starts the server"
  [& args]
  (let [handler (if (not (env/in-prod?))
                  (do (println "Server in dev. mode, running hot-reload")
                      (reload/wrap-reload (site #'all-routes)))
                  (do (println "Server in production mode")
                      (site all-routes)))]
    (run-server handler {:port port})
    (println "Running server on port " port)))

