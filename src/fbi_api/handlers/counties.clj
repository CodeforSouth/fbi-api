(ns fbi-api.handlers.counties
  (:require [fbi-api.validations :as validate]
            [fbi-api.util :as util]
            [fbi-api.db :as db]))

(defn handle-ok
  "Retrieves all counties from db"
  [ctx]
  {:meta {}
   :data (db/select-counties-summary)})
