(ns restaurant-inspections-api.constants
  (:require [restaurant-inspections-api.environment :as env]))

(def port (env/get-env-port))
(def chime (env/get-env-chime))
(def production? (env/production?))
(def db-url (env/get-env-db-url))
(def csv-files (env/get-csv-files))
