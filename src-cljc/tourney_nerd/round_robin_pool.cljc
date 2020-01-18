(ns tourney-nerd.round-robin-pool
  (:require
    [tourney-nerd.constructors :as constructors]
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

(assert (= (cycle-rows before-cycle-rows1) after-cycle-rows1))

(def example-4-teams
  [{:id "team-1001"
    :name "Team 1"
    :order 1}
   {:id "team-1002"
    :name "Team 2"
    :order 2}
   {:id "team-1003"
    :name "Team 3"
    :order 3}
   {:id "team-1004"
    :name "Team 4"
    :order 4}])

(def example-5-teams
  [{:id "team-1001"
    :name "Team 1"
    :order 1}
   {:id "team-1002"
    :name "Team 2"
    :order 2}
   {:id "team-1005"
    :name "Team 5"
    :order 5}
   {:id "team-1003"
    :name "Team 3"
    :order 3}
   {:id "team-1004"
    :name "Team 4"
    :order 4}])

;; TODO: move this fn to a util namespace
(defn teams->list
  "Convert teams into a list ordered by their seed (ie :order key)"
  [teams]
  (let [teams (if (map? teams) (vals teams) teams)]
    (sort-by :order teams)))

(defn- dummy-team? [t]
  (= t :dummy-team))

;; https://en.wikipedia.org/wiki/Round-robin_tournament#Scheduling_algorithm
;; If n is the number of competitors, a pure round robin tournament requires (n/2)*(n-1) games
(defn create-games
  "Returns a map of games that implement a Round-robin pool for the given teams."
  [{:keys [teams num-rounds]}]
  (let [teams (teams->list teams)
        num-teams (count teams)
        num-expected-games (* (half num-teams) (dec num-teams))
        optimal-num-rounds 5
        num-rounds (if (integer? num-rounds) num-rounds optimal-num-rounds)
        games (atom [])
        rows (atom (create-cycle-rows teams))]
    (while (< (count @games) num-expected-games)
      (dotimes [row-idx (count (:top @rows))]
        (let [teamA (nth (:top @rows) row-idx)
              teamB (nth (:bottom @rows) row-idx)]
          (when (and teamA teamB)
            (let [new-game (constructors/create-game {:teamA-id (:id teamA)
                                                      :teamB-id (:id teamB)
                                                      ; :name (str "Pool")})])
                                                      :division-id (:division-id teamA)})]
              (swap! games conj new-game)))))
      (swap! rows cycle-rows))
    ;; sanity-check that we produced the correct number of games
    (assert (= num-expected-games (count @games)) "Incorrect number of games for round-robin pool!")
    @games))
