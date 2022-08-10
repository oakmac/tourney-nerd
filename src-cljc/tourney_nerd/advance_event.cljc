(ns tourney-nerd.advance-event
  "Advance an event based on games played."
  (:require
    [clojure.set :as set]
    [clojure.string :as str]
    [clojure.walk :as walk]
    [taoensso.timbre :as timbre]
    [tourney-nerd.divisions :as divisions]
    [tourney-nerd.fields :as fields]
    [tourney-nerd.games :as games]
    [tourney-nerd.groups :as groups]
    [tourney-nerd.results :as results]
    [tourney-nerd.schedule :as schedule]
    [tourney-nerd.teams :as teams]
    [tourney-nerd.util :refer [half]]))

;;------------------------------------------------------------------------------
;; Misc

;; TODO: move these to the games ns
(defn- winner
  "Returns the game-id of the winning team. nil if the game is not finished"
  [game]
  (when (games/game-finished? game)
    (if (> (:scoreA game) (:scoreB game))
      (:teamA-id game)
      (:teamB-id game))))

(defn- loser
  "Returns the game-id of the losing team. nil if the game is not finished"
  [game]
  (when (games/game-finished? game)
    (if (< (:scoreA game) (:scoreB game))
      (:teamA-id game)
      (:teamB-id game))))

;;------------------------------------------------------------------------------
;; Predicates

;; TODO: this can be replaced using a set of sets:
;; #{ #{teamA teamB}
;;    #{teamA teamB}
;;    ...}
(defn teams-already-played?
  "Returns false or a game where the two teams have previously played."
  [teamA-id teamB-id all-games]
  (let [teamA-id (name teamA-id)
        teamB-id (name teamB-id)
        games-list (vals all-games)
        games-where-teams-played-each-other
          (filter #(or (and (= teamA-id (:teamA-id %)) (= teamB-id (:teamB-id %)))
                       (and (= teamB-id (:teamA-id %)) (= teamA-id (:teamB-id %))))
                  games-list)]
    (if (empty? games-where-teams-played-each-other)
      false
      (first games-where-teams-played-each-other))))

(defn is-swiss-game? [g]
  (integer? (:swiss-round g)))

(defn- has-pending-team? [game]
  (or (:pending-teamA game)
      (:pending-teamB game)))

;; TODO: pretty sure the loops in this function could be done cleaner as a reduce
(defn create-matchups
  "Calculates the next Swiss round matchups.
   Returns a vector of matchups. Each matchup is a vector of [teamA-id teamB-id]
   The vectors are in-order. ie: the top team is at (ffirst) position"
  [teams games swiss-round]
  (let [;; find games below the target swiss round
        games-to-look-at (filter #(and (is-swiss-game? (second %))
                                       (< (:swiss-round (second %)) swiss-round))
                                 games)
        ;; create a set of all the matchups that have occurred so far
        prior-matchups (reduce (fn [matchups game]
                                 (let [teamA-id (-> game :teamA-id name)
                                       teamB-id (-> game :teamB-id name)]
                                   (conj matchups #{teamA-id teamB-id})))
                               #{}
                               (vals games-to-look-at))
        ;; score the teams
        results (results/games->results teams games-to-look-at)
        ;; list to pull team-ids from
        sorted-team-ids (atom (vec (map :team-id results)))
        num-matchups-to-create (half (count @sorted-team-ids))
        ;; we will fill the new-matchups vector until the sorted-team-ids list is empty
        new-matchups (atom [])]
    ;; create the matchups for this swiss round
    (dotimes [i num-matchups-to-create]
      (let [;; take the first team in the list
            teamA-id (first @sorted-team-ids)
            ;; remove that team from the teams list
            _ (swap! sorted-team-ids subvec 1)
            ;; find the next closest team that has not already played teamA
            match-found? (atom false)
            j (atom 0)
            _ (while (and (not @match-found?)
                          (< @j (count @sorted-team-ids)))
                (let [team-id (nth @sorted-team-ids @j)
                      possible-matchup [teamA-id team-id]
                      possible-matchup-set (set possible-matchup)
                      teams-already-played? (contains? prior-matchups possible-matchup-set)]
                  (if teams-already-played?
                    (swap! j inc) ;; try the next team in the list
                    (do
                      ;; we found a match; exit this loop
                      (reset! match-found? true)
                      ;; add this matchup to the new-matchups vector
                      (swap! new-matchups conj possible-matchup)
                      ;; remove this team-id from the possible teams
                      ;; NOTE: this would faster using subvec
                      (swap! sorted-team-ids (fn [ids]
                                               (vec (remove #(= % team-id) ids))))))))]))
    @new-matchups))

;;------------------------------------------------------------------------------
;; Determine Next Swiss Round Matchups

(defn- advance-swiss-round
  "Advance a single Swiss Round if possible."
  [all-teams all-games round-to-advance]
  (let [next-round (inc round-to-advance)
        games-list (map (fn [[game-id game]] (assoc game :game-id game-id)) all-games)
        games-in-this-round (remove #(not= round-to-advance (:swiss-round %)) games-list)
        games-in-next-round (remove #(not= next-round (:swiss-round %)) games-list)
        next-round-game-ids (map :game-id games-in-next-round)
        all-games-finished? (every? games/game-finished? games-in-this-round)]
    (cond
      ;; TODO: make this less ugly
      all-games-finished?
      (let [next-round-matchups (create-matchups all-teams all-games next-round)
            new-games (atom all-games)]
        (doall
          (map-indexed (fn [idx [teamA-id teamB-id]]
                         (let [game-id (nth next-round-game-ids idx)]
                           (swap! new-games update-in [game-id] merge
                             {:teamA-id teamA-id
                              :teamB-id teamB-id})))
                       next-round-matchups))
        @new-games)

      ;; TODO: try to advance games based on simulation here

      ;; else just return the games in their current state
      :else
      all-games)))

(defn- games->swiss-rounds
  "Returns a set of the Swiss Rounds in games."
  [games]
  (reduce
    (fn [rounds game]
      (if (is-swiss-game? game)
        (conj rounds (:swiss-round game))
        rounds))
    #{}
    (vals games)))

(defn- advance-swiss-games
  "Given a tournament state, advances the Swiss games if possible."
  [state]
  (let [all-teams (:teams state)
        swiss-rounds-set (games->swiss-rounds (:games state))
        swiss-rounds-list (-> swiss-rounds-set vec sort)]
    ;; advance each round
    (reduce
      (fn [state round-num]
        (let [all-games (:games state)
              updated-games (advance-swiss-round all-teams all-games round-num)]
          (assoc state :games updated-games)))
      state
      ;; the last swiss round cannot "advance"; ignore it
      (drop-last swiss-rounds-list))))

;; TODO: re-write this function to be safer and more functional
(defn- advance-pending-game
  [event pending-game]
  (let [pending-game-id (:game-id pending-game)

        pending-teamA (:pending-teamA pending-game)
        teamA-target-game-id (:game-id pending-teamA)
        teamA-target-game (get-in event [:games teamA-target-game-id])
        teamA-target-game-finished? (games/game-finished? teamA-target-game)
        teamA-target-game-winner (winner teamA-target-game)
        teamA-target-game-loser (loser teamA-target-game)

        pending-teamB (:pending-teamB pending-game)
        teamB-target-game-id (:game-id pending-teamB)
        teamB-target-game (get-in event [:games teamB-target-game-id])
        teamB-target-game-finished? (games/game-finished? teamB-target-game)
        teamB-target-game-winner (winner teamB-target-game)
        teamB-target-game-loser (loser teamB-target-game)

        new-event (atom event)]

    (when (and (= (:type pending-teamA) "PENDING_GAME_RESULT")
               teamA-target-game-finished?)
      (swap! new-event assoc-in [:games pending-game-id :teamA-id]
             (if (= (:result pending-teamA) "winner-of")
               teamA-target-game-winner
               teamA-target-game-loser)))

    (when (and (= (:type pending-teamB "PENDING_GAME_RESULT"))
               teamB-target-game-finished?)
      (swap! new-event assoc-in [:games pending-game-id :teamB-id]
             (if (= (:result pending-teamB) "winner-of")
               teamB-target-game-winner
               teamB-target-game-loser)))

    @new-event))

(defn- advance-pending-games
  "Advances any pending games"
  [event]
  (let [all-games (:games event)
        games-list (map (fn [[game-id game]] (assoc game :game-id game-id)) all-games)
        games-with-pending-teams (filter has-pending-team? games-list)]
    (reduce
      advance-pending-game
      event
      games-with-pending-teams)))

(defn advance-event
  "Given a tournament state, tries to advance it.
   ie: calculates Swiss Round matchups, fills brackets, scores pools, etc"
  [event]
  (-> event
      advance-swiss-games
      advance-pending-games))
      ;; TODO: advance-bracket-games
