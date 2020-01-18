(ns tourney-nerd.constructors
  "Functions for creating the basic building blocks of an event."
  (:require
   [tourney-nerd.util :refer [create-uuid]]))

(defn- division-id []
  (str "division-" (create-uuid)))

(defn create-division [division-name]
  {:id (division-id)
   :name division-name})

(defn- field-id []
  (str "field-" (create-uuid)))

(defn create-field
  ([field-name]
   (create-field field-name nil))
  ([field-name description]
   {:id (field-id)
    :name field-name
    :description description}))

(defn- team-id []
  (str "team-" (create-uuid)))

;; {:name :captain :division-id}
(defn create-team [opts]
  (merge opts {:id (team-id)}))

(defn- round-id []
  (str "round-" (create-uuid)))

(defn create-round
  ([round-name]
   (create-round round-name nil nil))
  ([round-name start-time]
   (create-round round-name start-time nil))
  ([round-name start-time end-time]
   {:id (round-id)
    :name round-name
    :start-time start-time
    :end-time end-time}))

(defn- game-id []
  (str "game-" (create-uuid)))

(defn create-game [opts]
  (merge opts {:id (game-id)}))
