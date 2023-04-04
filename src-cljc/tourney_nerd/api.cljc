(ns tourney-nerd.api
  (:require
    [tourney-nerd.divisions :as tn.divisions]
    [tourney-nerd.event :as tn.event]
    [tourney-nerd.fields :as tn.fields]
    [tourney-nerd.games :as tn.games]
    [tourney-nerd.results :as tn.results]
    [tourney-nerd.schedule :as tn.schedule]))

(defn create-event
  "Creates an Event.
  Returns the new Event."
  ([]
   (tn.event/create-event {}))
  [arg1]
  (if (string? arg1)
    (tn.event/create-event {:name arg1})
    (tn.event/create-event arg1)))

(defn create-division
  "Creates and adds a new Division to an Event.
  Returns the updated Event"
  [evt division-name]
  (tn.divisions/create-division evt division-name))

;; TODO: update-divisions
;; TODO: get-divisions
;; TODO: get-sorted-divisions

;; TODO: create-fields
;; TODO: create-field
;; TODO: get-fields
;; TODO: get-sorted-fields
;; TODO: update-field

;; TODO: create-timeslot
;; TODO: update-timeslot
;; TODO: delete-timeslot
;; TODO: create-timeslots

;; TODO: create-team
;; TODO: create-teams
;; TODO: update-team
;; TODO: delete-team
;; TODO: get-teams-for-division

;; TODO: create-game-group
;; TODO: update-game-group
;; TODO: delete-game-group

;; TODO: update-game
;; TODO: get-games-for-game-group

;; TODO: get-results-for-division
;; TODO: get-results-for-game-group
;; TODO: get-results-for-team

;; TODO: all-games-finished-in-game-group?
;; TODO: get-sorted-games-for-day
