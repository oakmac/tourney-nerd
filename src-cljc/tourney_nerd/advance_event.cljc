(ns tourney-nerd.advance-event
  "Advance an event based on games played."
  (:require
    [tourney-nerd.games :as games]
    [tourney-nerd.groups :as groups]
    [tourney-nerd.results :as results]
    [tourney-nerd.util :refer [half]]))

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

(defn- has-pending-game-result?
  "Does this game have a pending-team that is pending a single game result?"
  [game]
  (or (= "PENDING_GAME_RESULT" (get-in game [:pending-teamA :type]))
      (= "PENDING_GAME_RESULT" (get-in game [:pending-teamB :type]))))

(defn- has-pending-group-result?
  "Does this game have a pending-team that is pending a group result?"
  [game]
  (or (= "PENDING_GROUP_RESULT" (get-in game [:pending-teamA :type]))
      (= "PENDING_GROUP_RESULT" (get-in game [:pending-teamB :type]))))

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
    (dotimes [_i num-matchups-to-create]
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
(defn- advance-pending-game-result
  "Advances a game that is pending a direct game result. ie: PENDING_GAME_RESULT"
  [event pending-game]
  (let [pending-game-id (:game-id pending-game)

        pending-teamA (:pending-teamA pending-game)
        teamA-target-game-id (:game-id pending-teamA)
        teamA-target-game (when teamA-target-game-id
                            (or (get-in event [:games (keyword teamA-target-game-id)])
                                (get-in event [:games (name teamA-target-game-id)])))

        teamA-target-game-finished? (games/game-finished? teamA-target-game)
        teamA-target-game-winner (results/winner teamA-target-game)
        teamA-target-game-loser (results/loser teamA-target-game)

        pending-teamB (:pending-teamB pending-game)
        teamB-target-game-id (:game-id pending-teamB)
        teamB-target-game (when teamB-target-game-id
                            (or (get-in event [:games (keyword teamB-target-game-id)])
                                (get-in event [:games (name teamB-target-game-id)])))
        teamB-target-game-finished? (games/game-finished? teamB-target-game)
        teamB-target-game-winner (results/winner teamB-target-game)
        teamB-target-game-loser (results/loser teamB-target-game)

        new-event (atom event)]
    (when (and (= (:type pending-teamA) "PENDING_GAME_RESULT")
               teamA-target-game-finished?)
      (swap! new-event assoc-in [:games pending-game-id :teamA-id]
             (if (= (:result pending-teamA) "winner-of")
               teamA-target-game-winner
               teamA-target-game-loser)))

    (when (and (= (:type pending-teamB) "PENDING_GAME_RESULT")
               teamB-target-game-finished?)
      (swap! new-event assoc-in [:games pending-game-id :teamB-id]
             (if (= (:result pending-teamB) "winner-of")
               teamB-target-game-winner
               teamB-target-game-loser)))

    @new-event))

;; TODO:
;; advance-pending-games and advance-pending-group-games functions could probably
;; be combined
(defn- advance-pending-games
  "Advances games that are pending single game results"
  [event]
  (let [all-games (:games event)
        games-list (map (fn [[game-id game]] (assoc game :game-id game-id)) all-games)
        games-with-pending-teams (filter has-pending-game-result? games-list)]
    (reduce
      advance-pending-game-result
      event
      games-with-pending-teams)))

;; TODO:
;; - this function is very ugly; needs a re-write
;; - also inefficient: should calculate group results once, then check games for advancement
(defn advance-pending-group-result
  "TODO: write docstring"
  [event pending-game]
  (let [pending-game-id (:game-id pending-game)
        group1-id (get-in pending-game [:pending-teamA :group-id])
        group2-id (get-in pending-game [:pending-teamB :group-id])
        group1-place (get-in pending-game [:pending-teamA :place])
        group2-place (get-in pending-game [:pending-teamB :place])
        group1-games (when group1-id (groups/get-all-games-for-group event group1-id))
        group2-games (when group2-id (groups/get-all-games-for-group event group2-id))
        group1-finished? (and (not (empty? group1-games)) (every? games/game-finished? (vals group1-games)))
        group2-finished? (and (not (empty? group2-games)) (every? games/game-finished? (vals group2-games)))
        group1-teams (when group1-finished? (groups/get-teams-for-group event group1-id))
        group2-teams (when group2-finished? (groups/get-teams-for-group event group2-id))
        group1-results (when group1-finished? (results/games->sorted-results group1-teams group1-games))
        group2-results (when group2-finished? (results/games->sorted-results group2-teams group2-games))
        group1-team-id (when (and group1-id group1-finished? group1-results (number? group1-place))
                         (:team-id (nth group1-results (dec group1-place) nil)))
        group2-team-id (when (and group2-id group2-finished? group2-results (number? group2-place))
                         (:team-id (nth group2-results (dec group2-place) nil)))]
    (cond-> event
      group1-team-id
      (assoc-in [:games pending-game-id :teamA-id] group1-team-id)

      group2-team-id
      (assoc-in [:games pending-game-id :teamB-id] group2-team-id))))

(defn- advance-pending-group-games
  "Advances games that are pending group results"
  [event]
  (let [all-games (:games event)
        games-list (map (fn [[game-id game]] (assoc game :game-id game-id)) all-games)
        games-with-pending-teams (filter has-pending-group-result? games-list)]
    (reduce
      advance-pending-group-result
      event
      games-with-pending-teams)))

(defn advance-event
  "Given a tournament state, tries to advance it.
   ie: calculates Swiss Round matchups, fills brackets, scores pools, etc"
  [event]
  (-> event
      advance-swiss-games
      advance-pending-games
      advance-pending-group-games))
