(ns com.oakmac.tourney-nerd.divisions
  (:require
   [com.oakmac.tourney-nerd.util.ids :as util.ids]
   [malli.core :as malli]))

(def division-schema
  [:map
   [:id [:re util.ids/division-id-regex]]
   [:name [:string {:min 3, :max 100}]]
   [:order [:int {:min 1}]]])

(defn create-division
  "creates a single Division"
  [order name]
  {:post [(malli/validate division-schema %)]}
  {:id (util.ids/create-division-id)
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
