(ns unit.fbi-api.handlers.counties-test
  (:require [fbi-api.handlers.counties :refer :all]
            [fbi-api.db :as db]
            [clojure.test :refer :all]))

(def ctx-mock {:request {:params {}}})

(def db-mock [{:district "D1"
               :countynumber 23
               :countyname "Dade"
               :inspections 20630}])

(def response-mock {:meta {}, :data db-mock})

(deftest handle-ok-test
  "expect handle-ok to query the db and return a formatted response"
  (with-redefs [db/select-counties-summary (fn [] db-mock)]
    (is (= response-mock (handle-ok ctx-mock)))))
