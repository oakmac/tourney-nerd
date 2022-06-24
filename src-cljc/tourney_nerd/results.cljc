(ns tourney-nerd.results
  (:require
    [taoensso.timbre :as timbre]
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

(def victory-points-for-a-win 30000)
(def victory-points-for-a-tie 10000)

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

(defn- team->result
  "Creates a result map for a single team."
  [teams games-vec team-id]
  (let [team (or (get teams team-id)
                 (get teams (keyword team-id)))
        games-this-team-has-played (filter #(and (game-finished? %)
                                                 (or (= (:teamA-id %) (name team-id))
                                                     (= (:teamB-id %) (name team-id))))
                                           games-vec)]
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

(defn compare-upa-tiebreaker-rules
  [resultA resultB]
  (let [a-games-played? (not (zero? (:games-played resultA)))
        b-games-played? (not (zero? (:games-played resultB)))
        a-record (:record resultA)
        b-record (:record resultB)
        same-record? (= a-record b-record)
        a-victory-points (:victory-points resultA)
        b-victory-points (:victory-points resultB)
        ;; NOTE: this will need to change when we adopt Rule 3
        a-tiebreaker-points (get-in resultA [:result-against-tied-teams :victory-points])
        b-tiebreaker-points (get-in resultB [:result-against-tied-teams :victory-points])]

    ;; defensive / sanity-check:
    (when (and same-record?
               (or (not (:result-against-tied-teams resultA)))
               (or (not (:result-against-tied-teams resultB))))
      (timbre/error "Two teams with the same record do NOT have result-against-tied-teams!"
                    "teamA-id:" (:team-id resultA)
                    "teamB-id:" (:team-id resultB)))

    (cond
      ;; teams that have played any games sort higher than teams that have played none
      (and a-games-played? (not b-games-played?)) -1
      (and b-games-played? (not a-games-played?)) 1

      ;; sort by record + point diff (ie: victory points) if teams have different records
      (not same-record?) (compare b-victory-points a-victory-points)

      ;; sort by tiebreaker rules
      same-record? (compare b-tiebreaker-points a-tiebreaker-points)

      :else
      0)))

(defn results->duplicate-records
  "Returns a Set of the duplicate records in a collection of results.
  ie: which records need tiebreaking logic applied"
  [results]
  (->> results
    (map :record)
    frequencies
    (filter (fn [[_record cnt]]
              (> cnt 1)))
    (map first)
    set))

;; TODO: this could probably have better performance, calculate once instead of
;; for every result / team
(defn add-result-against-teams-with-same-record
  "Adds a Result map against teams that have the same record. This is UPA tiebreaking rule 2:
  Won-loss records, counting only games between the teams that are tied."
  [result all-results all-teams all-games]
  (let [team-id (:team-id result)
        record (:record result)
        teams-with-same-record (->> all-results
                                 (filter
                                   #(= record (:record %)))
                                 (map :team-id)
                                 set)
        games-between-tied-teams (filter
                                   (fn [{:keys [teamA-id teamB-id]}]
                                     (and (contains? teams-with-same-record teamA-id)
                                          (contains? teams-with-same-record teamB-id)))
                                   (vals all-games))
        result-against-tied-teams (-> (team->result all-teams games-between-tied-teams team-id)
                                      add-record-to-result)]
    (assoc result :result-against-tied-teams result-against-tied-teams)))

;; TODO: could make this variadic and extract the teams from the games
(defn games->results
  "Returns a list of Results for the given teams + games."
  [teams games]
  (let [results (map (partial team->result teams (vals games)) (keys teams))
        results-with-records (map add-record-to-result results)
        records-that-need-tiebreaking (results->duplicate-records results-with-records)
        results3 (map
                   (fn [result]
                     (if (contains? records-that-need-tiebreaking (:record result))
                       (add-result-against-teams-with-same-record result results-with-records teams games)
                       result))
                   results-with-records)]
    results3))

(def default-tiebreak-method "TIEBREAK_UPA_RULES")

(defn sort-results
  "Sort a Result list using various tiebreaking methods."
  ([results]
   (sort-results results default-tiebreak-method))
  ([results sort-method]
   (case sort-method
     "TIEBREAK_VICTORY_POINTS" (sort compare-victory-points results)
     "TIEBREAK_UPA_RULES" (sort compare-upa-tiebreaker-rules results)
     (do (timbre/error "Unrecognized sort-method:" sort-method)
         (sort compare-victory-points results)))))

(defn games->sorted-results
  "Returns a sorted list of Results for the given teams + games."
  ([teams games]
   (games->sorted-results teams games default-tiebreak-method))
  ([teams games tiebreak-method]
   (let [results (games->results teams games)
         sorted-results (sort-results results tiebreak-method)]
     (map-indexed
       (fn [idx result]
         (assoc result :place (inc idx)))
       sorted-results))))
