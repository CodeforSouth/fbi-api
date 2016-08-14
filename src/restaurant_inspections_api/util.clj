(ns restaurant-inspections-api.util)

(defn str-null->int
  "convert from blank string to number or nil"
  [str]
  (try (Integer. (not-empty str)) (catch Exception _)))
