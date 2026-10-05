(ns com.oakmac.tourney-nerd.divisions
  (:require
   [com.oakmac.tourney-nerd.util :as util]
   [com.oakmac.tourney-nerd.util.ids :as util.ids]))

(defn valid-division?
  "Is d a valid Division?"
  [d]
  (and (map? d)
       (util.ids/division-id? (:id d))
       (util/string-of-length? (:name d) 3 100)
       (pos-int? (:order d))))

(defn create-division
  "creates a single Division"
  [order name]
  {:post [(valid-division? %)]}
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
