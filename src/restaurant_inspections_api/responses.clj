(ns restaurant-inspections-api.responses
  (:require [cheshire.core :as json]))

(def json-headers {"Content-Type" "application/json"})

(defn bad-request
  "Return status 400"
  [body]
  {:status 400
   :headers json-headers
   :body (json/generate-string body)})

(defn bad-response
  "Return status 500"
  [body]
  {:status 500
   :headers json-headers
   :body (json/generate-string body)})

(defn not-found
  "Return status 404"
  ([]
   (not-found [{:error "Resource not found"}]))
  ([body]
   {:status 404
    :headers json-headers
    :body (json/generate-string body)}))

(defn error-response
  "Return an error response with the given status and error message"
  [status message]
  {:status status
   :headers json-headers
   :body (json/generate-string {:error message})})

(defn ok
  "Return a common ok 200 response"
  [body]
  {:status 200
   :headers json-headers
   :body (json/generate-string body)})

(defn redirect
  "Redirects to the given url"
  [url]
  {:status 302
   :headers {:Location url}
   :body ""})
