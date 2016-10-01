(ns restaurant-inspections-api.routes-test
    (:require [clojure.test :refer :all]
              [restaurant-inspections-api.routes :refer [all-routes]]))

(defn request [resource web-app & params]
    (all-routes {:request-method :get :uri resource :params (first params)}))

(deftest test-routes
    (is (= 200 (:status (request "/greet" all-routes))))
    (is (= "Hello World!"
           (:body (request "/greet" all-routes))))
    (is (= 404 (:status (request "/name" all-routes)))))
