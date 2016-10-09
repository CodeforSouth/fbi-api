(ns restaurant-inspections-api.routes-test
  (:require [clojure.test :refer :all]
            [restaurant-inspections-api.routes :refer :all]))

(defn request [resource web-app & params]
  (all-routes {:request-method :get :uri resource :params (first params)}))

(deftest test-all-routes
  (testing "Returns 404 if route doesnt match"
    (is (= 404 (:status (request "/name" all-routes))))))
