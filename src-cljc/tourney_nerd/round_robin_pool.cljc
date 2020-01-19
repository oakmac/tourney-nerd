(ns tourney-nerd.round-robin-pool
  (:require
    [tourney-nerd.constructors :as constructors]
    [tourney-nerd.teams :as teams-fns]
    [tourney-nerd.util :refer [half]]))

(defn- create-cycle-rows [itms]
  (let [itms (vec itms)
        num-items (count itms)
        itms (if (odd? num-items) (conj itms nil) itms)]
    {:top (subvec itms 0 (half (count itms)))
     :bottom (-> (subvec itms (half (count itms)))
                 reverse
                 vec)}))

(def before-create-rows1
  ["a" "b" "c" "d" "e"])

(def after-create-rows1
  {:top ["a" "b" "c"]
   :bottom [nil "e" "d"]})

(def before-create-rows2
  ["a" "b" "c" "d" "e" "f"])

(def after-create-rows2
  {:top ["a" "b" "c"]
   :bottom ["f" "e" "d"]})

(assert (= (create-cycle-rows before-create-rows1) after-create-rows1))
(assert (= (create-cycle-rows before-create-rows2) after-create-rows2))

(defn- cycle-rows [rows]
  (let [top-row (:top rows)
        bottom-row (:bottom rows)
        first-item-top-row (first top-row)
        last-item-top-row (last top-row)
        first-item-bottom-row (first bottom-row)]
    {:top (->> top-row
               rest
               drop-last
               vec
               (concat [first-item-top-row first-item-bottom-row])
               vec)
     :bottom (-> bottom-row
                 vec
                 rest
                 vec
                 (conj last-item-top-row)
                 vec)}))

(def before-cycle-rows1
  {:top ["a" "b" "c" "d"]
   :bottom ["h" "g" "f" "e"]})

(def after-cycle-rows1
  {:top ["a" "h" "b" "c"]
   :bottom ["g" "f" "e" "d"]})

(def before-cycle-rows2
  {:top '("a" "b")
   :bottom [nil "c"]})

(def after-cycle-rows2
  {:top ["a" nil]
   :bottom ["c" "b"]})

(assert (= (cycle-rows before-cycle-rows1) after-cycle-rows1))
(assert (= (cycle-rows before-cycle-rows2) after-cycle-rows2))

(def example-4-teams
  [{:id "team-1001"
    :name "Team 1"
    :seed 1}
   {:id "team-1002"
    :name "Team 2"
    :seed 4}
   {:id "team-1003"
    :name "Team 3"
    :seed 6}
   {:id "team-1004"
    :name "Team 4"
    :seed 9}])

(def example-5-teams
  [{:id "team-1001"
    :name "Team 1"
    :seed 1}
   {:id "team-1002"
    :name "Team 2"
    :seed 2}
   {:id "team-1005"
    :name "Team 5"
    :seed 5}
   {:id "team-1003"
    :name "Team 3"
    :seed 3}
   {:id "team-1004"
    :name "Team 4"
    :seed 4}])

;; https://en.wikipedia.org/wiki/Round-robin_tournament#Scheduling_algorithm
(defn create-games
  "Returns a map of games that implement a Round-robin pool for the given teams."
  [{:keys [teams pool-name]}]
  (let [teams (->> (teams-fns/teams->sorted-by-seed teams)
                   (map-indexed (fn [idx team]
                                  (assoc team :pool-seed (inc idx)))))
        num-teams (count teams)
        ;; If n is the number of competitors, a pure round robin tournament requires (n/2)*(n-1) games
        num-expected-games (* (half num-teams) (dec num-teams))
        num-expected-rounds (if (even? num-teams)
                              ;; If n is even, then in each of (n-1) rounds
                              (dec num-teams)
                              ;; If n is odd, there will be n rounds
                              num-teams)
        games (atom [])
        rows (atom (create-cycle-rows teams))
        round-num (atom 1)]
    (while (< (count @games) num-expected-games)
      (dotimes [row-idx (count (:top @rows))]
        (let [teamA (nth (:top @rows) row-idx)
              teamB (nth (:bottom @rows) row-idx)]
          (when (and teamA teamB)
            (let [matchup (str (:pool-seed teamA) "v" (:pool-seed teamB))
                  new-game (constructors/create-game
                             (merge {:teamA-id (:id teamA)
                                     :teamB-id (:id teamB)
                                     :pool-round-num @round-num
                                     :pool-matchup matchup}
                                    (when (string? pool-name)
                                      {:name (str pool-name " - " matchup)})
                                    (when (:division-id teamA)
                                      {:division-id (:division-id teamA)})))]
              (swap! games conj new-game)))))
      (swap! rows cycle-rows)
      (swap! round-num inc))
    ;; sanity-check that we produced the correct number of games and rounds
    (assert (= num-expected-games (count @games)) "Incorrect number of games for round-robin pool!")
    (assert (= num-expected-rounds (dec @round-num)) "Incorrect number of rounds for round-robin pool!")
    ;; return a map of the games
    (zipmap (map :id @games) @games)))
