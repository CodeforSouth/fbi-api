(ns restaurant-inspections-api.core
  (:require [org.httpkit.server :refer [run-server]]
            [ring.middleware.reload :as reload]
            [compojure.middleware :as middleware]
            [compojure.handler :refer [site]]
            [taoensso.timbre :as log]
            [schema.core :as s]
            [compojure.api.sweet :as sweet]
            ;; internal
            [restaurant-inspections-api.constants :as const]
            [restaurant-inspections-api.cors :refer [all-cors]]
            [restaurant-inspections-api.routes :refer [all-routes]]
            ;; [restaurant-inspections-api.docs :as docs]
            [restaurant-inspections-api.cron.core :refer [load-api-data]])

  (:use [clojure.tools.nrepl.server :only (start-server stop-server)])
  (:gen-class))

(defn wrap-exception-handling
  [handler]
  (fn [request]
    (try
      (handler request)
      (catch com.mysql.jdbc.exceptions.jdbc4.CommunicationsException comm-e
        (log/error comm-e "DATABASE NOT AVAILABLE!")
        {:status 500 :body "INTERNAL SERVER ERROR: Database not available. Please contact the administrator:
        Github: teh0xqb
        Email: quilesbaker@gmail.com
        or Code For Miami Slack Group (http://codefor.miami)"})
      (catch Exception e
        (log/error e "GENERIC ERROR bubbled up. replace specific expection here and add to the catch statements.")
        {:status 500 :body "INTERNAL SERVER ERROR"}))))

(s/defschema User {:id s/Str,
                   :name s/Str
                   :address {:street s/Str
                             :city (s/enum :tre :hki)}})

(def api (->> all-routes
              middleware/wrap-canonical-redirect
              wrap-exception-handling
              site
              all-cors
              (sweet/api {:swagger
                          {:ui "/api-docs"
                           :spec "/swagger.json"
                           :data {:info {:title "Sausages",
                                         :version "1.0.0",
                                         :description "Sausage description",
                                         :termsOfService "http://helloreverb.com/terms/",
                                         :contact {:name "My API Team",
                                                   :email "foo@example.com",
                                                   :url "http://www.metosin.fi"},
                                         :license {:name "Eclipse Public License",
                                                   :url "http://www.eclipse.org/legal/epl-v10.html"}},
                                  :produces ["application/json"],
                                  :consumes ["application/json"],
                                  :tags [{:name "user", :description "User stuff"}],
                                  :paths {"/api/ping" {:get {:responses {"200" {:schema User
                                                                                :description "Found it!"}}}}},
                                  :definitions {"User" {:type "object",
                                                        :properties {:id {:type "string"},
                                                                     :name {:type "string"},
                                                                     :address {:ref "#/definitions/UserAddress"}},
                                                        :additionalProperties false,
                                                        :required (:id :name :address)}}}}})))

(def app
  (if const/production?
    api
    (reload/wrap-reload api)))

(defn -main
  "Starts server and schedules load-api-data process."
  [& args]
  (log/info (str "Running server on port " const/port))
  (load-api-data)
  (defonce nrepl-server (start-server :port 7888))
  (run-server app {:port const/port}))
