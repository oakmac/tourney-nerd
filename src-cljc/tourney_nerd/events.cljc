(ns tourney-nerd.events
  (:require
    [clojure.set :as set]
    [clojure.string :as str]
    [clojure.walk :as walk]
    [taoensso.timbre :as timbre]
    [tourney-nerd.divisions :as divisions]
    [tourney-nerd.fields :as fields]
    [tourney-nerd.games :as games]
    [tourney-nerd.groups :as groups]
    [tourney-nerd.schedule :as schedule]
    [tourney-nerd.teams :as teams]))

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
(defn clone
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
