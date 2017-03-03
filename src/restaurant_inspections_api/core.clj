(ns restaurant-inspections-api.core
  (:require [org.httpkit.server :refer [run-server]]
            [ring.middleware.reload :as reload]
            [compojure.middleware :as middleware]
            [compojure.handler :refer [site]]
            [taoensso.timbre :as log]
            [compojure.api.sweet :as sweet]
            ;; internal
            [restaurant-inspections-api.constants :as const]
            [restaurant-inspections-api.cors :refer [all-cors]]
            [restaurant-inspections-api.routes :refer [routes]]
            [restaurant-inspections-api.schemas :as schemas]
            [restaurant-inspections-api.cron.core :refer [load-api-data]])
  (:use [clojure.tools.nrepl.server :only (start-server stop-server)])
  (:gen-class))

(def server-error-details
  "\n\nPlease contact the administrator:
       - Github: teh0xqb
       - Email: quilesbaker@gmail.com
       - or Code For Miami Slack Group (http://codefor.miami)")

(defn wrap-exception-handling
  [handler]
  (fn [request]
    (try
      (handler request)
      (catch com.mysql.jdbc.exceptions.jdbc4.CommunicationsException comm-e
        (log/error comm-e "DATABASE NOT AVAILABLE!")
        {:status 500 :body (str "500 INTERNAL SERVER ERROR: Database not available." server-error-details)})
      (catch Exception e
        (log/error e "GENERIC ERROR bubbled up. Replace specific expection here and add to the catch statements.")
        {:status 500 :body (str "500 INTERNAL SERVER ERROR." server-error-details)}))))

(def api (->> (middleware/wrap-canonical-redirect           ;; Not using thread last in first arg
               routes                                       ;; because handler is the second args of this middleware
               (fn [^String uri]
                 (if (.equals uri "/")
                   uri
                   (middleware/remove-trailing-slash uri))))
              wrap-exception-handling
              site
              all-cors
              (sweet/api schemas/swagger)))

(def app
  (if const/production?
    api
    (reload/wrap-reload #'api)))

(defn -main
  "Starts server and schedules load-api-data process."
  [& args]
  (log/info (str "Running server on port " const/port))
  (load-api-data)
  (defonce nrepl-server (start-server :port 7888))
  (run-server app {:port const/port}))
