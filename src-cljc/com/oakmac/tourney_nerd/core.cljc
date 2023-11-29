(ns com.oakmac.tourney-nerd.core
  (:require
    [com.oakmac.tourney-nerd.divisions]
    [com.oakmac.tourney-nerd.events]
    [com.oakmac.tourney-nerd.fields]
    [com.oakmac.tourney-nerd.games]
    [com.oakmac.tourney-nerd.groups]
    [com.oakmac.tourney-nerd.schedule]
    [com.oakmac.tourney-nerd.teams]))

; (defn valid-score? [score]
;   (and (integer? score)
;        (>= score 0)))

;;------------------------------------------------------------------------------
;; Misc

; (defn- winner
;   "Returns the game-id of the winning team."
;   [game]
;   (if (> (:scoreA game) (:scoreB game))
;     (:teamA-id game)
;     (:teamB-id game)))

; (defn- loser
;   "Returns the game-id of the losing team."
;   [game]
;   (if (< (:scoreA game) (:scoreB game))
;     (:teamA-id game)
;     (:teamB-id game)))

;;------------------------------------------------------------------------------
;; Ensure Tournament Structure

; (defn- ensure-game-status
;   "Game status must be valid."
;   [game]
;   (if-not (contains? game-statuses (:status game))
;     (assoc game :status scheduled-status)
;     game))

; (defn- ensure-scores
;   "Game scores must be integers."
;   [game]
;   (let [game2 (if-not (valid-score? (:scoreA game))
;                 (assoc game :scoreA 0)
;                 game)]
;     (if-not (valid-score? (:scoreB game2))
;       (assoc game2 :scoreB 0)
;       game2)))

(defn- ensure-games
  "Games must have scores, status, and ids"
  [state]
  ;; TODO: write this
  state)

(defn- ensure-teams
  "Teams must have..."
  [state]
  ;; TODO: write this
  state)

(defn ensure-tournament-state [state]
  (-> state
      ensure-teams
      ensure-games))

;; -----------------------------------------------------------------------------
;; FIXME: move all of this to a CLJS JS interop namespace

; (defn- js-games->results
;   "JavaScript wrapper around games->results"
;   [js-teams js-games]
;   (let [teams (js->clj js-teams :keywordize-keys true)
;         games (js->clj js-games :keywordize-keys true)]
;     (clj->js (games->results teams games))))
;
; (js/goog.exportSymbol "calculateResults" js-games->results)
;
; (defn- js-advance-tournament
;   "JavaScript wrapper around advance-tournament"
;   [js-state]
;   (-> js-state
;       js->clj
;       keywordize-keys
;       advance-tournament
;       clj->js))
;
; (js/goog.exportSymbol "advanceTournament" js-advance-tournament)
