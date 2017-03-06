(ns fbi-api.core
  (:require [org.httpkit.server :refer [run-server]]
            [ring.middleware.reload :as reload]
            [compojure.middleware :as middleware]
            [compojure.handler :refer [site]]
            [taoensso.timbre :as log]
            [compojure.api.sweet :as sweet]
            ;; internal
            [fbi-api.constants :as const]
            [fbi-api.cors :refer [all-cors]]
            [fbi-api.routes :refer [routes]]
            [fbi-api.schemas :as schemas]
            [fbi-api.cron.core :refer [load-api-data]])
  (:use [clojure.tools.nrepl.server :only (start-server stop-server)])
  (:gen-class))

(when-not const/production?
  ;; First time running, or changes detected in "src" code
  (println "Hot reloading http dev server before processing next request..."))

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

(defn start-http-api
  ""
  [port]
  (log/info (str "Running server on port " port))
  (run-server app {:port port}))

(defn -main
  "Starts server and schedules load-api-data process."
  [& args]
  (load-api-data)
  (defonce nrepl-server (start-server :port 7888))
  (start-http-api const/port))
