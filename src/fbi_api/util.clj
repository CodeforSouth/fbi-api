(ns fbi-api.util
  (:require
   [taoensso.timbre :as timbre] ;; Possible refers:
   ;;       :refer (log  trace  debug  info  warn  error  fatal  report
   ;;               logf tracef debugf infof warnf errorf fatalf reportf
   ;;               spy get-env log-env)
   [taoensso.timbre.appenders.core :as appenders]
   [clj-time.coerce :as coerce-time]
   [clj-time.format :as timef]
   [clj-time.core :as clj-time]))

(defn with-abs-path
  "Gets absolute path of server file."
  [filename]
  (str (.getCanonicalPath (clojure.java.io/file ".")) (java.io.File/separator) filename))

(def timbre-config
  {:level :debug  ; e/o #{:trace :debug :info :warn :error :fatal :report}
   ;; Control log filtering by namespaces/patterns. Useful for turning off
   ;; logging in noisy libraries, etc.:
   ;;    :ns-whitelist  [] #_["my-app.foo-ns"]
   :ns-blacklist  [] #_["taoensso.*"]
   :middleware [] ; (fns [data]) -> ?data, applied left->right
   ;; Clj only:
   ;;    :timestamp-opts default-timestamp-opts ; {:pattern _ :locale _ :timezone _}
   ;;    :output-fn default-output-fn ; (fn [data]) -> string

   ;; TODO: Environmental variable to specify log file path
   :appenders {:spit (appenders/spit-appender {:fname (with-abs-path "fbi_api.log")})}})

(timbre/merge-config! timbre-config)

(defn todays-date
  "Get todays date."
  []
  (timef/unparse (timef/formatter "yyyy-MM-dd") (clj-time/now)))

(defn str-csv-date->iso
  "Convert csv date format to iso date."
  [stri]
  (try (not-empty (timef/unparse (timef/formatter "YYYY-MM-dd")
                                 (timef/parse (timef/formatter "MM/dd/YYYY") stri)))
       (catch Exception _)))

(defn str-null->int
  "Convert from blank string to number or nil."
  [str]
  (try (Integer. (not-empty str)) (catch Exception _)))

(defn parse-date-or-nil
  "Parses a date object into a string, returns nil if null"
  [date]
  (when-not (nil? date)
    (timef/unparse (timef/formatter "YYYY-MM-dd")
                   (coerce-time/from-date date))))

(defn format-query-params-error
  "Receives param-key for an error and returns a map with error details."
  [param-key]
  {:code 1
   :title "Validation Error"
   :detail "Invalid format or value for parameter."
   :source {:parameter param-key}})
