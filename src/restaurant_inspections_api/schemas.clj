(ns restaurant-inspections-api.schemas
  (:require [schema.core :as s]))

(s/defschema User {:id s/Str,
                   :name s/Str
                   :address {:street s/Str
                             :city (s/enum :tre :hki)}})

;; "district": "D1",
;; "countynumber": 23,
;; "countyname": "Dade",
;; "inspections": 20630
(s/defschema County {:district s/Str,
                     :countynumber s/Int
                     :countyname s/Str
                     :inspections s/Int})

(defn wrap-data
  ""
  [data]
  {:meta []
   :data [data]})

(def swagger
  {:swagger
   {:ui "/api-docs"
    :spec "/swagger.json"
    :data {:info {:title "Florida Restaurant Inspections API",
                  :version "0.3.0",
                  :description "Florida RESTAPI",
                  ;; :termsOfService "",
                  :contact {:name "Joel Quiles",
                            :email "quilesbaker@gmail.com",
                            :url "//codefor.miami"},
                  :license {:name "MIT License",
                            :url "https://choosealicense.com/licenses/mit/"}},
           :produces ["application/json"],
           :consumes ["application/json"],
           :tags [{:name "inspection", :description "inspection stuff"}],

           :paths {"/counties"  {:get {:responses {200 {:schema County
                                                        :description "Counties description here..."}}
                                       :summary "counties summary here"}}

                   "/inspections" {:get {:responses {200 {:schema User
                                                          :description "Found it!"}}

                                         :parameters {:query {(s/optional-key :countyNumber) s/Int
                                                              (s/optional-key :districtCode) s/Int
                                                              (s/optional-key :zipCodes) s/Str
                                                              (s/optional-key :startDate) s/Str
                                                              (s/optional-key :endDate) s/Str
                                                              (s/optional-key :businessName) s/Str}}

                                         :summary "adds two numbers together"}}

                   "/inspections/:id" {:get {:responses {200 {:schema User
                                                              :description "Found it!"}}
                                             :summary "adds two numbers together"}}

                   "/businesses" {:get {:responses {200 {:schema User
                                                         :description "Found it!"}}
                                        :summary "adds two numbers together"}}

                   "/businesses/:licenseNumber" {:get {:responses {200 {:schema User
                                                                        :description "Found it!"}}
                                                       :summary "adds two numbers together"}}

                   "/violations" {:get {:responses {200 {:schema User
                                                         :description "Found it!"}}
                                        :summary "adds two numbers together"}}

                   }}}})
