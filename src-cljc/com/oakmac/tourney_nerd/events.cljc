(ns com.oakmac.tourney-nerd.events
  (:require
    [clojure.set :as set]
    [clojure.string :as str]
    [clojure.walk :as walk]
    [com.oakmac.tourney-nerd.divisions :as divisions]
    [com.oakmac.tourney-nerd.fields :as fields]
    [com.oakmac.tourney-nerd.games :as games]
    [com.oakmac.tourney-nerd.groups :as groups]
    [com.oakmac.tourney-nerd.order :refer [ensure-items-order]]
    [com.oakmac.tourney-nerd.schedule :as schedule]
    [com.oakmac.tourney-nerd.teams :as teams]
    [taoensso.timbre :as timbre]))

;; TODO: we need an "event integrity" function
;; - all games timeslots are on the schedule
;; - all game team IDs are valid
;; - all games divisions match the teams playing
;; - all fields + timeslots are unique (no double-booked fields)
;; - scheduled games cannot have scores

(defn new-random-id [id]
  (let [[head _tail] (str/split id #"-")]
    (case head
      "division" (divisions/random-division-id)
      "timeslot" (schedule/random-timeslot-id)
      "team" (teams/random-team-id)
      "field" (fields/random-field-id)
      "game" (games/random-game-id)
      "group" (groups/random-group-id)
      (timbre/warn "Unrecognized id type:" id))))

;; TODO: What else to reset here?
(defn clone-event
  "Clones an existing event format. Returns a new Event based on the input event.
  All ids will be unique and values reset to defaults appropriate for a new Event."
  [event]
  (let [old-ids (set/union
                  (->> event :divisions vals (map :id) set)
                  (->> event :fields vals (map :id) set)
                  (->> event :games vals (map :id) set)
                  (->> event :groups vals (map :id) set)
                  (->> event :schedule vals (map :id) set)
                  (->> event :teams vals (map :id) set))

        ;; map of "old-id" -> "new-id"
        new-ids (reduce
                  (fn [new-ids old-id]
                    (assoc new-ids old-id (new-random-id old-id)))
                  {}
                  old-ids)]

    ;; walk the event structure and replace things
    (walk/postwalk
      (fn [itm]
        (cond
          ;; replace ids
          (and (string? itm) (contains? new-ids itm))
          (get new-ids itm)

          (and (keyword? itm) (contains? new-ids (name itm)))
          (keyword (get new-ids (name itm)))

          ;; reset objects
          (games/game? itm)
          (games/reset-game itm)

          (teams/team? itm)
          (teams/reset-team itm)

          :else itm))
      event)))

(defn ensure-order
  "Ensures that all items with :order fields are sequential."
  [event]
  (-> event
    (update-in [:divisions] ensure-items-order)
    (update-in [:fields] ensure-items-order)
    (update-in [:groups] groups/ensure-groups-order)))
