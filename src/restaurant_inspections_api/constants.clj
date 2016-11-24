(ns restaurant-inspections-api.constants
  (:require [restaurant-inspections-api.environment :as env]))

(def ^:const port (env/get-env-port))
(def ^:const chime (env/get-env-chime))
(def ^:const production? (env/production?))
(def ^:const db-url (env/get-env-db-url))
(def ^:const csv-files (env/get-csv-files))
