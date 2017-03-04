(ns integration.fbi-api.core-test
  (:require [clojure.test :refer :all]
            [org.httpkit.client :as http]
            [clojure.data.json :as json]
            [fbi-api.core :refer :all]))

;;; =========================== TEST HELPERS ====================

(defonce server (atom nil))

(defn stop-server []
  (when-not (nil? @server)
    ;; graceful shutdown: wait 100ms for existing requests to be finished
    ;; :timeout is optional, when no timeout, stop immediately
    (@server :timeout 100)
    (reset! server nil)))

(defn random-port
  "Returns a random port from 8800 to 8900"
  []
  (+ (rand-int 100) 8000))

(defn test-endpoint [path port data-count meta-map]
  (let [url (str "http://localhost:" port path)
        res @(http/get url)]
    (println (str "Trying url " url))
    (are [a b] (= a b)
      (:error res) nil
      (:status res) 200)
    (let [body (json/read-str (:body res))]
      ;; (println (str "Type of body response: " (type body)))
      ;; => class clojure.lang.PersistentArrayMap
      (if (not (nil? meta-map))
        (is (= meta-map (get body "meta"))))
      (is (= data-count (count (get body "data")))))))

;;; ========================= START TESTS =================

(deftest ^{:integration true} start-http-api-test
  (testing "API Integration test all endpoints for basic stats"
    (try
      (let [port (random-port)]
        (reset! server (start-http-api port))

        (test-endpoint "/inspections" port 20 nil)
        (test-endpoint "/businesses" port 20 nil)
        (test-endpoint "/counties" port 67 {})
        (test-endpoint "/violations" port 58 {}))

      (finally
        (println "Stopping test server")
        (stop-server)))))
