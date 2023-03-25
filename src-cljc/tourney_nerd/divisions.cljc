(ns tourney-nerd.divisions
  (:require
    [malli.core :as malli]
    [tourney-nerd.util.base58 :refer [random-base58]]))

(def division-id-regex
  #"^division-[a-zA-Z0-9]{4,}$")

(defn random-division-id []
  (str "division-" (random-base58)))

(def division-schema
  [:map
   [:id [:re division-id-regex]]
   [:name [:string {:min 3, :max 100}]]
   [:order [:int {:min 1}]]])

(defn create-division
  "creates a single Division"
  [order name]
  {:post [(malli/validate division-schema %)]}
  {:id (random-division-id)
   :name name
   :order order})

(defn create-divisions
  "returns a map of Divisions from a list of Division Names
  used for Event creation"
  [names]
  (let [new-divisions (map-indexed
                        (fn [idx name]
                          (create-division (inc idx) name))
                        names)]
    (zipmap (map :id new-divisions) new-divisions)))

;; TODO: good candidate for unit tests
(defn get-first-division-id
  "returns the first division-id from an event"
  [event]
  (->> event
       :divisions
       vals
       (sort-by :order)
       first
       :id))
