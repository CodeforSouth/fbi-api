(ns integration.fbi-api.core-test
  (:require [clojure.test :refer :all]
            [org.httpkit.client :as http]
            [clojure.data.json :as json]
            [fbi-api.core :refer :all]))

;;; =========================== TEST HELPERS ====================

(defn random-port
  "Returns a random port from 8800 to 8900"
  []
  (+ (rand-int 100) 8000))

(defonce server (atom nil))
(defonce port (random-port))

(defn stop-server []
  (when-not (nil? @server)
    ;; graceful shutdown: wait 100ms for existing requests to be finished
    ;; :timeout is optional, when no timeout, stop immediately
    (@server :timeout 100)
    (reset! server nil)))

(defn test-endpoint [path {:keys [status data-count meta-map]}]
  (let [url (str "http://localhost:" port path)
        res @(http/get url)]
    (println (str "Trying url " url))
    (are [a b] (= a b)
      (:error res) nil
      (:status res) status)
    (let [body (json/read-str (:body res))]
      ;; (println (str "Type of body response: " (type body)))
      ;; => class clojure.lang.PersistentArrayMap
      (when-not (nil? meta-map)
        (is (= meta-map (get body "meta"))))
      (when-not (nil? data-count)
        (is (= data-count (count (get body "data"))))))))

;;; ========================= START TESTS =================

;; TODO: add fixtures
(deftest ^{:integration true} start-http-api-test
  (testing "API Integration test all endpoints for basic stats"
    (try
      (reset! server (start-http-api port))

      ;; perPage defaults to 20, should find 20 inspections
      (test-endpoint "/inspections" {:status 200, :data-count 20})

      ;; really random :id, should not find an inspection
      (test-endpoint "/inspections/3425443434345" {:status 200, :data-count 0, :meta-map {}})

      ;; Not sure how many would show on this zip code, but perPage=5 so data should contain 5, we hope
      (test-endpoint "/inspections?zipCodes=33129&perPage=5" {:status 200, :data-count 5})

      ;; same as /inspections endpoint
      (test-endpoint "/businesses" {:status 200, :data-count 20})

      ;; same as :id for /inspections. should return 0 on data
      (test-endpoint "/businesses/345543483545" {:status 200, :data-count 0})

      (test-endpoint "/counties" {:status 200, :data-count 67, :meta-map {}})
      (test-endpoint "/violations" {:status 200, :data-count 58, :meta-map {}})

      (test-endpoint "/test404" {:status 404})

      (finally
        (println "Stopping test server.")
        (stop-server)))))
