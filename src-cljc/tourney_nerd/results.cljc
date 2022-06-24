(ns tourney-nerd.results
  (:require
    [tourney-nerd.games :refer [game-finished?]]))

;;------------------------------------------------------------------------------
;; Misc

(defn winner
  "Returns the game-id of the winning team."
  [game]
  (if (> (:scoreA game) (:scoreB game))
    (:teamA-id game)
    (:teamB-id game)))

(defn loser
  "Returns the game-id of the losing team."
  [game]
  (if (< (:scoreA game) (:scoreB game))
    (:teamA-id game)
    (:teamB-id game)))

;;------------------------------------------------------------------------------
;; Calculate Results

(def victory-points-for-a-win 3000)
(def victory-points-for-a-tie 1000)

(def empty-result
  {:games-lost 0
   :games-played 0
   :games-tied 0
   :games-won 0
   :points-diff 0
   :points-lost 0
   :points-played 0
   :points-won 0
   :record ""
   :team-captain nil
   :team-id nil
   :team-name nil
   :victory-points 0})

(defn- add-game-to-result [result game]
  (let [{:keys [team-id games-won games-lost games-tied games-played
                points-won points-lost points-played points-diff victory-points]}
        result

        {:keys [teamA-id teamB-id scoreA scoreB]}
        game

        ;; make sure all the team-ids are strings
        team-id (name team-id)
        teamA-id (name teamA-id)
        teamB-id (name teamB-id)

        won? (or (and (= team-id teamA-id) (> scoreA scoreB))
                 (and (= team-id teamB-id) (> scoreB scoreA)))
        lost? (or (and (= team-id teamA-id) (< scoreA scoreB))
                  (and (= team-id teamB-id) (< scoreB scoreA)))
        tied? (= scoreA scoreB)
        scored-for (if (= team-id teamA-id) scoreA scoreB)
        scored-against (if (= team-id teamA-id) scoreB scoreA)]
    (assoc result
      :games-lost (if lost? (inc games-lost) games-lost)
      :games-played (inc games-played)
      :games-tied (if tied? (inc games-tied) games-tied)
      :games-won (if won? (inc games-won) games-won)
      :points-diff (+ points-diff scored-for (* -1 scored-against))
      :points-lost (+ points-lost scored-against)
      :points-played (+ points-played scoreA scoreB)
      :points-won (+ points-won scored-for)
      :victory-points (+ victory-points
                         (if won? victory-points-for-a-win 0)
                         (if tied? victory-points-for-a-tie 0)
                         scored-for
                         (* -1 scored-against)))))

(defn- team->results
  "Creates a result map for a single team."
  [teams games team-id]
  (let [team (or (get teams team-id)
                 (get teams (keyword team-id)))
        games-this-team-has-played (filter #(and (game-finished? %)
                                                 (or (= (:teamA-id %) (name team-id))
                                                     (= (:teamB-id %) (name team-id))))
                                           (vals games))]
    (reduce add-game-to-result
            (assoc empty-result :team-captain (:captain team)
                                :team-id (name team-id)
                                :team-name (:name team)
                                :team-seed (:seed team))
            games-this-team-has-played)))

(defn- compare-victory-points [a b]
  (let [a-games-played? (not (zero? (:games-played a)))
        b-games-played? (not (zero? (:games-played b)))
        a-victory-points (:victory-points a)
        b-victory-points (:victory-points b)]
    (cond
      (and a-games-played? (not b-games-played?))
      -1

      (and b-games-played? (not a-games-played?))
      1

      (> a-victory-points b-victory-points)
      -1

      (> b-victory-points a-victory-points)
      1

      :else
      0)))

(defn add-record-to-result
  "Adds a record string to a result"
  [{:keys [games-won games-lost games-tied] :as result}]
  (assoc result :record (str games-won "-" games-lost "-" games-tied)))

;; TODO: refactor this to not need teams; should extract team-ids from games
(defn games->results
  "Creates a results list for all the teams."
  [teams games]
  (let [results (map (partial team->results teams games) (keys teams))
        results-with-records (map add-record-to-result results)
        sorted-results (sort compare-victory-points results-with-records)]
    (map-indexed #(assoc %2 :place (inc %1)) sorted-results)))
