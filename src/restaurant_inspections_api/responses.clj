(ns restaurant-inspections-api.responses
  (:require [cheshire.core :as json]))

(def json-headers {"Content-Type" "application/json"})

(defn bad-request
  "return status 400"
  [body]
  {:status 400
   :headers json-headers
   :body (json/generate-string body)})

(defn bad-response
  "return status 500"
  [body]
  {:status 500
   :headers json-headers
   :body (json/generate-string body)})

(defn error-response
  "return an error response with the given status and error message"
  [status message]
  {:status status
   :headers json-headers
   :body (json/generate-string {:error message})})

(defn ok
  "return a common ok 200 response"
  [body]
  {:status 200
   :headers json-headers
   :body (json/generate-string body)})

(defn redirect
  "redirects to the given url"
  [url]
  {:status 302
   :headers {"Location" url}
   :body ""})
