(ns tourney-nerd.event
  (:require
    [tourney-nerd.util.base58 :refer [random-base58]]))

(defn random-event-id []
  (str "event-" (random-base58)))

(def default-event-name
  "My Event")

(defn create-event
  "Returns a new Event map."
  [opts]
  ;; TODO: validate opts here
  (merge
    {:name default-event-name}
    opts
    {:divisions {}
     :fields {}
     :game-groups {}
     :games {}
     :id (random-event-id)
     :schedule {}
     :teams {}}))
