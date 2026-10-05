(ns com.oakmac.tourney-nerd.fields
  (:require
   [com.oakmac.tourney-nerd.util :as util]
   [com.oakmac.tourney-nerd.util.ids :as util.ids]))

(defn valid-field?
  "Is f a valid Field?"
  [f]
  (and (map? f)
       (util.ids/field-id? (:id f))
       (util/string-of-length? (:name f) 1 100)
       (pos-int? (:order f))))
       ;; TODO: add optional description field here

(defn create-field
  "creates a single Field"
  [order name]
  {:post [(valid-field? %)]}
  {:id (util.ids/create-field-id)
   :name name
   :order order})

(defn get-field-by-id
  "Returns the Field with field-id from an Event, nil otherwise.
  The Event may be keyed by string or keyword."
  [event field-id]
  (util/get-by-id (or (:fields event) (get event "fields")) field-id))

(defn create-n-fields
  "returns a map of N Fields; used for Event creation"
  [num-fields]
  (let [fields-list (map-indexed
                      (fn [idx _n]
                        (create-field (inc idx) (str "Field " (inc idx))))
                      (range 0 num-fields))]
    (zipmap (map :id fields-list) fields-list)))
