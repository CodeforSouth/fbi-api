(ns restaurant-inspections-api.handlers.counties
  (:require [restaurant-inspections-api.validations :as validate]
            [restaurant-inspections-api.util :as util]
            [restaurant-inspections-api.db :as db]))

(defn handle-ok
  "Retrieves all counties from db"
  [ctx]
  {:meta {}
   :data (db/select-counties-summary)})
