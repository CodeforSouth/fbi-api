(ns unit.fbi-api.routes-test
  (:require [clojure.test :refer :all]
            [fbi-api.routes :refer :all]))

(defn request [resource web-app & params]
  (routes {:request-method :get :uri resource :params (first params)}))

(deftest test-routes
  (testing "Returns 404 if route doesnt match"
    (is (= 404 (:status (request "/name" routes))))))
