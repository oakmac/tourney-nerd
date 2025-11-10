(ns com.oakmac.tourney-nerd.fields
  (:require
   [com.oakmac.tourney-nerd.util.ids :as util.ids]
   [malli.core :as malli]))

(def field-schema
  [:map
   [:id [:re util.ids/field-id-regex]]
   [:name [:string {:min 1, :max 100}]]
   [:order [:int {:min 1}]]])
   ;; TODO: add optional description field here

(defn create-field
  "creates a single Field"
  [order name]
  {:post [(malli/validate field-schema %)]}
  {:id (util.ids/create-field-id)
   :name name
   :order order})

(defn create-n-fields
  "returns a map of N Fields; used for Event creation"
  [num-fields]
  (let [fields-list (map-indexed
                      (fn [idx _n]
                        (create-field (inc idx) (str "Field " (inc idx))))
                      (range 0 num-fields))]
    (zipmap (map :id fields-list) fields-list)))
