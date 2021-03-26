(ns tourney-nerd.fields
  (:require
    [clojure.set :as set]
    [malli.core :as malli]
    [tourney-nerd.util.base58 :refer [random-base58]]))

(def field-id-regex
  #"^field-[a-zA-Z0-9]{4,}$")

(defn field-id
  "returns a fresh field id"
  []
  (str "field-" (random-base58)))

(def field-schema
  [:map
   [:id [:re field-id-regex]]
   [:name [:string {:min 3, :max 100}]]
   [:order [:int {:min 1}]]])

(defn create-field
  "creates a single Field"
  [order name]
  {:post [(malli/validate field-schema %)]}
  {:id (field-id)
   :name name
   :order order})

(defn create-n-fields
  "returns a map of N Fields; used for Event creation"
  [num-fields]
  (let [fields-list (map-indexed
                      (fn [idx n]
                        (create-field (inc idx) (str "Field " (inc idx))))
                      (range 0 num-fields))]
    (zipmap (map :id fields-list) fields-list)))
