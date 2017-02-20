(ns restaurant-inspections-api.schemas
  (:require [schema.core :as s]))

(s/defschema User {:id s/Str,
                   :name s/Str
                   :address {:street s/Str
                             :city (s/enum :tre :hki)}})

(def swagger {:swagger
              {:ui "/api-docs"
               :spec "/swagger.json"
               :data {:info {:title "Florida Restaurant Inspections API",
                             :version "0.3.0",
                             :description "Florida RESTAPI",
                             ;; :termsOfService "",
                             :contact {:name "Joel Quiles",
                                       :email "quilesbaker@gmail.com",
                                       :url "codefor.miami"},
                             :license {:name "Eclipse Public License",
                                       :url "http://www.eclipse.org/legal/epl-v10.html"}},
                      :produces ["application/json"],
                      :consumes ["application/json"],
                      :tags [{:name "user", :description "User stuff"}],
                      :paths {"/inspections" {:get {:responses {200 {:schema User
                                                                     :description "Found it!"}}
                                                    :summary "adds two numbers together"}}}}}})
