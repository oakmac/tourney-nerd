(ns com.oakmac.tourney-nerd.results-test
  (:require
   [clojure.test :refer [deftest is testing]]
   [com.oakmac.tourney-nerd.results :as results :refer [games->results games->sorted-results group->tiebreaking-method]]
   [com.oakmac.tourney-nerd.test-util :refer [load-test-resource-json-file]]))

;; TODO: move all this data to .json or .edn files?

(def six-teams
  {"team-1"
   {:id "team-1"
    :name "A"
    :seed 1}
   "team-2"
   {:id "team-2"
    :name "B"
    :seed 2}
   "team-3"
   {:id "team-3"
    :name "C"
    :seed 3}
   "team-4"
   {:id "team-4"
    :name "D"
    :seed 4}
   "team-5"
   {:id "team-5"
    :name "E"
    :seed 5}
   "team-6"
   {:id "team-6"
    :name "F"
    :seed 6}})

;; Table 6.1.1 - Six-team round-robin – version 1
(def example21-games
  {;; 1v3 2v5 4v6
   "game-100"
   {:teamA-id "team-1"
    :teamB-id "team-3"
    :teamA-name "A"
    :teamB-name "C"
    :scoreA 3
    :scoreB 7
    :status "STATUS_FINAL"}
   "game-101"
   {:teamA-id "team-2"
    :teamB-id "team-5"
    :teamA-name "B"
    :teamB-name "E"
    :scoreA 5
    :scoreB 4
    :status "STATUS_FINAL"}
   "game-102"
   {:teamA-id "team-4"
    :teamB-id "team-6"
    :teamA-name "D"
    :teamB-name "F"
    :scoreA 5
    :scoreB 4
    :status "STATUS_FINAL"}

   ;; 1v5 2v4 3v6
   "game-200"
   {:teamA-id "team-1"
    :teamB-id "team-5"
    :teamA-name "A"
    :teamB-name "E"
    :scoreA 5
    :scoreB 4
    :status "STATUS_FINAL"}
   "game-201"
   {:teamA-id "team-2"
    :teamB-id "team-4"
    :teamA-name "B"
    :teamB-name "D"
    :scoreA 5
    :scoreB 4
    :status "STATUS_FINAL"}
   "game-202"
   {:teamA-id "team-3"
    :teamB-id "team-6"
    :teamA-name "C"
    :teamB-name "F"
    :scoreA 5
    :scoreB 4
    :status "STATUS_FINAL"}

   ;; 1v6 2v3 4v5
   "game-300"
   {:teamA-id "team-1"
    :teamB-id "team-6"
    :teamA-name "A"
    :teamB-name "F"
    :scoreA 8
    :scoreB 4
    :status "STATUS_FINAL"}
   "game-301"
   {:teamA-id "team-2"
    :teamB-id "team-3"
    :teamA-name "B"
    :teamB-name "C"
    :scoreA 5
    :scoreB 4
    :status "STATUS_FINAL"}
   "game-302"
   {:teamA-id "team-4"
    :teamB-id "team-5"
    :teamA-name "D"
    :teamB-name "E"
    :scoreA 2
    :scoreB 7
    :status "STATUS_FINAL"}

   ;; 1v4 2v6 3v5
   "game-400"
   {:teamA-id "team-1"
    :teamB-id "team-4"
    :teamA-name "A"
    :teamB-name "D"
    :scoreA 4
    :scoreB 5
    :status "STATUS_FINAL"}
   "game-401"
   {:teamA-id "team-2"
    :teamB-id "team-6"
    :teamA-name "B"
    :teamB-name "F"
    :scoreA 2
    :scoreB 5
    :status "STATUS_FINAL"}
   "game-402"
   {:teamA-id "team-3"
    :teamB-id "team-5"
    :teamA-name "C"
    :teamB-name "E"
    :scoreA 5
    :scoreB 4
    :status "STATUS_FINAL"}

   ;; 1v2 3v4 5v6
   "game-500"
   {:teamA-id "team-1"
    :teamB-id "team-2"
    :teamA-name "A"
    :teamB-name "B"
    :scoreA 5
    :scoreB 4
    :status "STATUS_FINAL"}
   "game-501"
   {:teamA-id "team-3"
    :teamB-id "team-4"
    :teamA-name "C"
    :teamB-name "D"
    :scoreA 5
    :scoreB 4
    :status "STATUS_FINAL"}
   "game-502"
   {:teamA-id "team-5"
    :teamB-id "team-6"
    :teamA-name "E"
    :teamB-name "F"
    :scoreA 8
    :scoreB 6
    :status "STATUS_FINAL"}})

;; Table 6.1.1 - Six-team round-robin – version 1
(def example22-games
  {;; 1v3 2v5 4v6
   "game-100"
   {:teamA-id "team-1"
    :teamB-id "team-3"
    :teamA-name "A"
    :teamB-name "C"
    :scoreA 5
    :scoreB 4
    :status "STATUS_FINAL"}
   "game-101"
   {:teamA-id "team-2"
    :teamB-id "team-5"
    :teamA-name "B"
    :teamB-name "E"
    :scoreA 5
    :scoreB 4
    :status "STATUS_FINAL"}
   "game-102"
   {:teamA-id "team-4"
    :teamB-id "team-6"
    :teamA-name "D"
    :teamB-name "F"
    :scoreA 5
    :scoreB 4
    :status "STATUS_FINAL"}

   ;; 1v5 2v4 3v6
   "game-200"
   {:teamA-id "team-1"
    :teamB-id "team-5"
    :teamA-name "A"
    :teamB-name "E"
    :scoreA 5
    :scoreB 4
    :status "STATUS_FINAL"}
   "game-201"
   {:teamA-id "team-2"
    :teamB-id "team-4"
    :teamA-name "B"
    :teamB-name "D"
    :scoreA 5
    :scoreB 4
    :status "STATUS_FINAL"}
   "game-202"
   {:teamA-id "team-3"
    :teamB-id "team-6"
    :teamA-name "C"
    :teamB-name "F"
    :scoreA 5
    :scoreB 4
    :status "STATUS_FINAL"}

   ;; 1v6 2v3 4v5
   "game-300"
   {:teamA-id "team-1"
    :teamB-id "team-6"
    :teamA-name "A"
    :teamB-name "F"
    :scoreA 4
    :scoreB 8
    :status "STATUS_FINAL"}
   "game-301"
   {:teamA-id "team-2"
    :teamB-id "team-3"
    :teamA-name "B"
    :teamB-name "C"
    :scoreA 5
    :scoreB 4
    :status "STATUS_FINAL"}
   "game-302"
   {:teamA-id "team-4"
    :teamB-id "team-5"
    :teamA-name "D"
    :teamB-name "E"
    :scoreA 2
    :scoreB 7
    :status "STATUS_FINAL"}

   ;; 1v4 2v6 3v5
   "game-400"
   {:teamA-id "team-1"
    :teamB-id "team-4"
    :teamA-name "A"
    :teamB-name "D"
    :scoreA 4
    :scoreB 5
    :status "STATUS_FINAL"}
   "game-401"
   {:teamA-id "team-2"
    :teamB-id "team-6"
    :teamA-name "B"
    :teamB-name "F"
    :scoreA 2
    :scoreB 5
    :status "STATUS_FINAL"}
   "game-402"
   {:teamA-id "team-3"
    :teamB-id "team-5"
    :teamA-name "C"
    :teamB-name "E"
    :scoreA 5
    :scoreB 4
    :status "STATUS_FINAL"}

   ;; 1v2 3v4 5v6
   "game-500"
   {:teamA-id "team-1"
    :teamB-id "team-2"
    :teamA-name "A"
    :teamB-name "B"
    :scoreA 5
    :scoreB 4
    :status "STATUS_FINAL"}
   "game-501"
   {:teamA-id "team-3"
    :teamB-id "team-4"
    :teamA-name "C"
    :teamB-name "D"
    :scoreA 5
    :scoreB 4
    :status "STATUS_FINAL"}
   "game-502"
   {:teamA-id "team-5"
    :teamB-id "team-6"
    :teamA-name "E"
    :teamB-name "F"
    :scoreA 8
    :scoreB 6
    :status "STATUS_FINAL"}})

;; Table 6.1.1 - Six-team round-robin – version 1
(def example31-games
  {;; 1v3 2v5 4v6
   "game-100"
   {:teamA-id "team-1"
    :teamB-id "team-3"
    :teamA-name "A"
    :teamB-name "C"
    :scoreA 13
    :scoreB 15
    :status "STATUS_FINAL"}
   "game-101"
   {:teamA-id "team-2"
    :teamB-id "team-5"
    :teamA-name "B"
    :teamB-name "E"
    :scoreA 5
    :scoreB 4
    :status "STATUS_FINAL"}
   "game-102"
   {:teamA-id "team-4"
    :teamB-id "team-6"
    :teamA-name "D"
    :teamB-name "F"
    :scoreA 5
    :scoreB 4
    :status "STATUS_FINAL"}

   ;; 1v5 2v4 3v6
   "game-200"
   {:teamA-id "team-1"
    :teamB-id "team-5"
    :teamA-name "A"
    :teamB-name "E"
    :scoreA 5
    :scoreB 4
    :status "STATUS_FINAL"}
   "game-201"
   {:teamA-id "team-2"
    :teamB-id "team-4"
    :teamA-name "B"
    :teamB-name "D"
    :scoreA 5
    :scoreB 4
    :status "STATUS_FINAL"}
   "game-202"
   {:teamA-id "team-3"
    :teamB-id "team-6"
    :teamA-name "C"
    :teamB-name "F"
    :scoreA 5
    :scoreB 4
    :status "STATUS_FINAL"}

   ;; 1v6 2v3 4v5
   "game-300"
   {:teamA-id "team-1"
    :teamB-id "team-6"
    :teamA-name "A"
    :teamB-name "F"
    :scoreA 4
    :scoreB 8
    :status "STATUS_FINAL"}
   "game-301"
   {:teamA-id "team-2"
    :teamB-id "team-3"
    :teamA-name "B"
    :teamB-name "C"
    :scoreA 15
    :scoreB 12
    :status "STATUS_FINAL"}
   "game-302"
   {:teamA-id "team-4"
    :teamB-id "team-5"
    :teamA-name "D"
    :teamB-name "E"
    :scoreA 8
    :scoreB 6
    :status "STATUS_FINAL"}

   ;; 1v4 2v6 3v5
   "game-400"
   {:teamA-id "team-1"
    :teamB-id "team-4"
    :teamA-name "A"
    :teamB-name "D"
    :scoreA 6
    :scoreB 5
    :status "STATUS_FINAL"}
   "game-401"
   {:teamA-id "team-2"
    :teamB-id "team-6"
    :teamA-name "B"
    :teamB-name "F"
    :scoreA 2
    :scoreB 5
    :status "STATUS_FINAL"}
   "game-402"
   {:teamA-id "team-3"
    :teamB-id "team-5"
    :teamA-name "C"
    :teamB-name "E"
    :scoreA 5
    :scoreB 9
    :status "STATUS_FINAL"}

   ;; 1v2 3v4 5v6
   "game-500"
   {:teamA-id "team-1"
    :teamB-id "team-2"
    :teamA-name "A"
    :teamB-name "B"
    :scoreA 15
    :scoreB 10
    :status "STATUS_FINAL"}
   "game-501"
   {:teamA-id "team-3"
    :teamB-id "team-4"
    :teamA-name "C"
    :teamB-name "D"
    :scoreA 5
    :scoreB 4
    :status "STATUS_FINAL"}
   "game-502"
   {:teamA-id "team-5"
    :teamB-id "team-6"
    :teamA-name "E"
    :teamB-name "F"
    :scoreA 8
    :scoreB 6
    :status "STATUS_FINAL"}})

;; TODO: add test for results->duplicate-records
; (deftest results-duplicate-records-test
;   (is (= (results->duplicate-records))))

(def woodlands-spring-league (load-test-resource-json-file "2025-woodlands-spring-league.json"))
(def woodlands-fall-league1 (load-test-resource-json-file "2025-woodlands-fall-league.before.json"))
(def woodlands-fall-league-2025 (load-test-resource-json-file "2025-woodlands-fall-league.json"))

(deftest sort-results-by-upa-rules-test
  ;; NOTE: I changed this example to be 3-2 instead of 4-2 and second/third place
  ;; instead of third/fourth place in order to reduce the number of example games required.
  ;; -- C. Oakman, 25 June 2022
  (testing "Example 2.1. A and B are tied for second place at 3-2, and during the
tournament, A has beaten B. Then, A gets second place and B gets third place. When
only two teams are involved, this rule is commonly called 'head-to-head.'"
    (is (= (let [results (games->results six-teams example21-games)]
             (-> (zipmap (map :team-name results)
                         (map :record results))
                 (select-keys ["A" "B"])))
           {"A" "3-2-0"
            "B" "3-2-0"}))
    (is (= (let [all-results (games->sorted-results six-teams example21-games)
                 second-and-third [(nth all-results 1) (nth all-results 2)]]
             (->> second-and-third (map :team-name) vec))
           ["A" "B"])))

  (testing "Example 2.2. A, B, and C, are tied for first place; they are all 3-2 after the
six team round-robin. A has beaten both B and C, while B has beaten C. The records
among the three teams only are: A is 2-0, B is 1-1, and C is 0-2. A finishes first,
B finishes second, and C finishes third."
    (is (= (let [results (games->results six-teams example22-games)]
             (-> (zipmap (map :team-name results)
                         (map :record results))
                 (select-keys ["A" "B" "C"])))
           {"A" "3-2-0"
            "B" "3-2-0"
            "C" "3-2-0"}))
    (is (= (->> (games->sorted-results six-teams example22-games)
                (take 3)
                (map :team-name)
                vec)
           ["A" "B" "C"])))

  (testing "Example 3.1. A, B, C are in a three-way tie for first place. A has beaten B
  15-10, B has beaten C 15-12, and C has beaten A, 15-13. A's point differential,
  then, is +5 and -2, which equals +3. B's is -2 and C's is -1. A finishes first, C
  finishes second, and B finishes third. Note that the three point differentials, in
  this case, +3,-2,-1, must always add up to zero. Note also that we do not use the
  point differential to choose the winner and then go 'head to head' to choose the
  other two. This would be a violation of Rule #1, which says that we must apply a
  tie-breaker rule equally to all the teams that are tied."
    (testing "A, B, C game point diffs must be exact"
      (let [ab-game (get example31-games "game-500")]
        (is (and (= 15 (:scoreA ab-game)) (= "A" (:teamA-name ab-game))))
        (is (and (= 10 (:scoreB ab-game)) (= "B" (:teamB-name ab-game)))))
      (let [bc-game (get example31-games "game-301")]
        (is (and (= 15 (:scoreA bc-game)) (= "B" (:teamA-name bc-game))))
        (is (and (= 12 (:scoreB bc-game)) (= "C" (:teamB-name bc-game)))))
      (let [ca-game (get example31-games "game-100")]
        (is (and (= 13 (:scoreA ca-game)) (= "A" (:teamA-name ca-game))))
        (is (and (= 15 (:scoreB ca-game)) (= "C" (:teamB-name ca-game))))))

    (let [results (games->sorted-results six-teams example31-games)
          abc-records (-> (zipmap (map :team-name results)
                                  (map :record results))
                          (select-keys ["A" "B" "C"]))
          abc-tied-results (-> (zipmap (map :team-name results)
                                       (map :result-against-tied-teams results))
                               (select-keys ["A" "B" "C"]))]
      (testing "the records of A, B, C must be the same"
        (is (= 1 (-> abc-records vals set count))))
      (testing "results against tied teams point diff must sum to zero"
        (is (= 0 (apply + (->> abc-tied-results vals (map :points-diff))))))
      (testing "sorted place must be correct"
        (is (= (->> results
                    (take 3)
                    (map :team-name)
                    vec)
               ["A" "C" "B"]))))))

  ; (testing "Example 3.2. A, B, C are in a three-way tie for first place. A has beaten B
  ; 15-11, B has beaten C 15-12, and C has beaten A, 15-13. A's point differential,
  ; then, is +4 and -2, which equals +2. B's is -1 and C's is -1. A takes first place.
  ; B and C are still tied. When, after the application of a rule, there are still
  ; teams that are tied, we go back to rule 2. Since B beat C, B takes 2nd place, and C
  ; takes 3rd. At this point we do not go onto rule 4."
  ;   (testing "A, B, C game point diffs must be exact"
  ;     (let [ab-game (get example31-games "game-500")]
  ;       (is (and (= 15 (:scoreA ab-game)) (= "A" (:teamA-name ab-game))))
  ;       (is (and (= 10 (:scoreB ab-game)) (= "B" (:teamB-name ab-game)))))
  ;     (let [bc-game (get example31-games "game-301")]
  ;       (is (and (= 15 (:scoreA bc-game)) (= "B" (:teamA-name bc-game))))
  ;       (is (and (= 12 (:scoreB bc-game)) (= "C" (:teamB-name bc-game)))))
  ;     (let [ca-game (get example31-games "game-100")]
  ;       (is (and (= 13 (:scoreA ca-game)) (= "A" (:teamA-name ca-game))))
  ;       (is (and (= 15 (:scoreB ca-game)) (= "C" (:teamB-name ca-game))))))
  ;
  ;   (let [results (games->sorted-results six-teams example31-games)
  ;         abc-records (-> (zipmap (map :team-name results)
  ;                                 (map :record results))
  ;                         (select-keys ["A" "B" "C"]))
  ;         abc-tied-results (-> (zipmap (map :team-name results)
  ;                                      (map :result-against-tied-teams results))
  ;                              (select-keys ["A" "B" "C"]))]
  ;     (testing "the records of A, B, C must be the same"
  ;       (is (= 1 (-> abc-records vals set count))))
  ;     (testing "results against tied teams point diff must sum to zero"
  ;       (= 0 (apply + (->> abc-tied-results vals (map :points-diff)))))
  ;     (testing "sorted place must be correct"
  ;       (is (= (->> results
  ;                   (take 3)
  ;                   (map :team-name)
  ;                   vec)
  ;              ["A" "C" "B"]))))))

;; This is the same thing as example22-games above, but tweaked slightly to give
;; A, B, C the same overall point diff
(def example22-games-point-diff-adjusted
  (-> example22-games
    (assoc-in ["game-101" :scoreA] 7)
    (assoc-in ["game-200" :scoreA] 8)))

;; add some additional games between B and C to make them super-tied
;; see "point differential in the head-to-head games" below
(def example22-games-b-c-adjusted
  (-> example22-games-point-diff-adjusted
    ;; have B and C play another game so their head-to-head record is tied
    (assoc "game-600" {:teamA-id "team-2"
                       :teamB-id "team-3"
                       :teamA-name "B"
                       :teamB-name "C"
                       :scoreA 4
                       :scoreB 6
                       :status "STATUS_FINAL"})
    ;; give B another win
    (assoc "game-601" {:teamA-id "team-2"
                       :teamB-id "team-4"
                       :teamA-name "B"
                       :teamB-name "D"
                       :scoreA 7
                       :scoreB 4
                       :status "STATUS_FINAL"})
    ;; give C another loss
    (assoc "game-602" {:teamA-id "team-4"
                       :teamB-id "team-3"
                       :teamA-name "D"
                       :teamB-name "C"
                       :scoreA 6
                       :scoreB 5
                       :status "STATUS_FINAL"})))

(def results-21games (games->results six-teams example21-games))
(def results-22games (games->results six-teams example22-games))
(def results-22games-point-diff-adjusted (games->results six-teams example22-games-point-diff-adjusted))

(deftest sort-results-by-woodlands-league-rules-test
  (testing "Win/Loss record"
    (is (= (-> (zipmap (map :team-name results-21games)
                       (map :record results-21games))
               (select-keys ["A" "C"]))
           {"A" "3-2-0"
            "C" "4-1-0"}))
    (let [all-results (games->sorted-results six-teams example21-games "TIEBREAK_WOODLANDS_LEAGUE_RULES")
          only-team-names (map :team-name all-results)
          only-a-and-c (filter #{"A" "C"} only-team-names)]
      (is (= only-a-and-c ["C" "A"])
          "C has a better record than A")))

  (testing "Point Differential (PD) – The difference between points scored and points allowed during the season"
    (is (= (-> (zipmap (map :team-name results-22games)
                       (map :record results-22games))
               (select-keys ["A" "B" "C"]))
           {"A" "3-2-0"
            "B" "3-2-0"
            "C" "3-2-0"})
        "A,B,C have the same overall record")
    (is (= (-> (zipmap (map :team-name results-22games)
                       (map :points-diff results-22games))
               (select-keys ["A" "B" "C"]))
           {"A" -2
            "B" -1
            "C" 1}))
    (let [all-results (games->sorted-results six-teams example22-games "TIEBREAK_WOODLANDS_LEAGUE_RULES")
          only-team-names (map :team-name all-results)
          only-a-b-c (filter #{"A" "B" "C"} only-team-names)]
      (is (= only-a-b-c ["C" "B" "A"])
          "point diff determines place")))

  (testing "Total Points Scored (PF) – The total number of points a team has scored during the season."
    (is (= (-> (zipmap (map :team-name results-22games-point-diff-adjusted)
                       (map :record results-22games-point-diff-adjusted))
               (select-keys ["A" "B" "C"]))
           {"A" "3-2-0"
            "B" "3-2-0"
            "C" "3-2-0"})
        "A,B,C have the same overall record")
    (is (= (-> (zipmap (map :team-name results-22games-point-diff-adjusted)
                       (map :points-diff results-22games-point-diff-adjusted))
               (select-keys ["A" "B" "C"]))
           {"A" 1
            "B" 1
            "C" 1})
        "A,B,C have the same overall point diff")
    (is (= (-> (zipmap (map :team-name results-22games-point-diff-adjusted)
                       (map :points-won results-22games-point-diff-adjusted))
               (select-keys ["A" "B" "C"]))
           {"A" 26
            "B" 23
            "C" 23}))
    (let [all-results (games->sorted-results six-teams example22-games-point-diff-adjusted "TIEBREAK_WOODLANDS_LEAGUE_RULES")
          only-team-names (map :team-name all-results)
          only-a-b (filter #{"A" "B"} only-team-names)
          only-a-c (filter #{"A" "C"} only-team-names)]
      (is (= only-a-b ["A" "B"]) "A has scored more points than B")
      (is (= only-a-c ["A" "C"]) "A has scored more points than C")))

  (testing "head-to-head record between the two teams"
    (let [all-results (games->sorted-results six-teams example22-games-point-diff-adjusted "TIEBREAK_WOODLANDS_LEAGUE_RULES")
          results-for-B (first (filter #(= "B" (:team-name %)) all-results))
          results-for-C (first (filter #(= "C" (:team-name %)) all-results))
          only-team-names (map :team-name all-results)
          only-b-c (filter #{"B" "C"} only-team-names)
          ;; NOTE: this reduce is just a filter
          games-played-between-b-and-c (reduce
                                         (fn [acc [game-id {:keys [teamA-id teamB-id] :as game}]]
                                           (if (= #{"team-2" "team-3"} (set [teamA-id teamB-id]))
                                             (assoc acc game-id game)
                                             acc))
                                         {}
                                         example22-games-point-diff-adjusted)
          results-for-games-between-b-and-c (games->sorted-results
                                              (select-keys six-teams ["team-2" "team-3"])
                                              games-played-between-b-and-c
                                              "TIEBREAK_WOODLANDS_LEAGUE_RULES")
          results-for-B-vs-C (first (filter #(= "B" (:team-name %)) results-for-games-between-b-and-c))
          results-for-C-vs-B (first (filter #(= "C" (:team-name %)) results-for-games-between-b-and-c))]
      ;; B and C have the same overall record, same overall point diff, and same overall points-won
      (is (= (:record results-for-B) (:record results-for-C)))
      (is (= (:points-diff results-for-B) (:points-diff results-for-C)))
      (is (= (:points-won results-for-B) (:points-won results-for-C)))

      ;; but B beat C in game-301, so B comes out ahead
      (is (= "1-0-0" (:record results-for-B-vs-C)))
      (is (= "0-1-0" (:record results-for-C-vs-B)))
      (is (= only-b-c ["B" "C"]))))

  (testing "point differential in the head-to-head games"
    (let [all-results (games->sorted-results six-teams example22-games-b-c-adjusted "TIEBREAK_WOODLANDS_LEAGUE_RULES")
          results-for-B (first (filter #(= "B" (:team-name %)) all-results))
          results-for-C (first (filter #(= "C" (:team-name %)) all-results))
          only-team-names (map :team-name all-results)
          only-b-c (filter #{"B" "C"} only-team-names)
          ;; NOTE: this reduce is just a filter
          games-played-between-b-and-c (reduce
                                         (fn [acc [game-id {:keys [teamA-id teamB-id] :as game}]]
                                           (if (= #{"team-2" "team-3"} (set [teamA-id teamB-id]))
                                             (assoc acc game-id game)
                                             acc))
                                         {}
                                         example22-games-b-c-adjusted)
          results-for-games-between-b-and-c (games->sorted-results
                                              (select-keys six-teams ["team-2" "team-3"])
                                              games-played-between-b-and-c
                                              "TIEBREAK_WOODLANDS_LEAGUE_RULES")
          results-for-B-vs-C (first (filter #(= "B" (:team-name %)) results-for-games-between-b-and-c))
          results-for-C-vs-B (first (filter #(= "C" (:team-name %)) results-for-games-between-b-and-c))]
      ;; B and C have the same overall record, same overall point diff, same overall points-won, same head-to-head record
      (is (= (:record results-for-B) (:record results-for-C)))
      (is (= (:points-diff results-for-B) (:points-diff results-for-C)))
      (is (= (:points-won results-for-B) (:points-won results-for-C)))

      ;; B and C have the same head-to-head record
      (is (= (:record results-for-B-vs-C) (:record results-for-C-vs-B)))

      ;; but C has a greater point diff against B (see game-600), so C comes out ahead
      (is (> (:points-diff results-for-C-vs-B) (:points-diff results-for-B-vs-C)))
      (is (= only-b-c ["C" "B"]))))

  (testing "test some real-world games from 2025 Woodlands Spring League"
    (let [results (games->sorted-results
                    (:teams woodlands-spring-league)
                    (:games woodlands-spring-league)
                    "TIEBREAK_WOODLANDS_LEAGUE_RULES")
          results-reduced (map #(select-keys % [:team-name :record :points-diff]) results)]
      (is (= results-reduced
             [{:team-name "Doom & Bloom"    :record "6-2-0" :points-diff 29}
              {:team-name "Trophy Husbands" :record "6-2-0" :points-diff 2}
              {:team-name "Springbreakers"  :record "4-4-0" :points-diff -2}
              {:team-name "Honey Huckers"   :record "4-4-0" :points-diff -6}
              {:team-name "Claritin Clear"  :record "2-6-0" :points-diff -10}
              {:team-name "Pushing Daisies" :record "2-6-0" :points-diff -13}])))))

(def woodlands-spring-league-before (load-test-resource-json-file "2025-woodlands-spring-league.before.json"))

(deftest group->tiebreaking-method-test
  (is (= (group->tiebreaking-method woodlands-spring-league-before "group-eyrwqfahB1ZX")
         "TIEBREAK_WOODLANDS_LEAGUE_RULES"))
  (is (= (group->tiebreaking-method woodlands-spring-league-before "group-does-not-exist")
         "TIEBREAK_UPA_RULES")))

(deftest group-results-test
  (testing "returns nil if not all games are finished"
    (let [league-with-scheduled-game (assoc-in woodlands-fall-league-2025
                                       [:games :game-JMmoZDwtzj91 :status]
                                       "STATUS_SCHEDULED")]
      (is (nil? (results/group->sorted-results league-with-scheduled-game "group-94pXoxYJWzBL")))))
  (testing "results from a round robin group - USAU tiebreaker rules"
    (let [league-with-usau-tiebreaking-method (assoc-in woodlands-fall-league-2025
                                                [:groups :group-FBT18rCWLFej :tiebreaking-method]
                                                "TIEBREAK_UPA_RULES")]
      (is (= (->> (results/group->sorted-results league-with-usau-tiebreaking-method "group-FBT18rCWLFej")
               (map #(select-keys % [:place :team-name :team-id])))
             [{:place 1, :team-name "Spirits of the Game", :team-id "team-Q3H4Pr6eEf3c"}
              {:place 2, :team-name "Sweater Weather",     :team-id "team-yasy1hnnku8t"}
              {:place 3, :team-name "Huck-O-Lanterns",     :team-id "team-5Qaw8MxNJMAz"}
              {:place 4, :team-name "Discaffeinated",      :team-id "team-wD1jVxJdmjkZ"}
              {:place 5, :team-name "Huckleberry Pie",     :team-id "team-vfzgApxUkmEL"}
              {:place 6, :team-name "Headless Horsemen",   :team-id "team-S5hApBgb9pA5"}]))))
  (testing "results from a round robin group - Woodlands tiebreaker rules"
    (is (= (->> (results/group->sorted-results woodlands-fall-league-2025 "group-FBT18rCWLFej")
             (map #(select-keys % [:place :team-name :team-id])))
           [{:place 1, :team-name "Huck-O-Lanterns",     :team-id "team-5Qaw8MxNJMAz"}
            {:place 2, :team-name "Sweater Weather",     :team-id "team-yasy1hnnku8t"}
            {:place 3, :team-name "Spirits of the Game", :team-id "team-Q3H4Pr6eEf3c"}
            {:place 4, :team-name "Discaffeinated",      :team-id "team-wD1jVxJdmjkZ"}
            {:place 5, :team-name "Huckleberry Pie",     :team-id "team-vfzgApxUkmEL"}
            {:place 6, :team-name "Headless Horsemen",   :team-id "team-S5hApBgb9pA5"}])))
  (testing "results from a bracket group"
    (is (= (results/group->sorted-results woodlands-fall-league-2025 "group-94pXoxYJWzBL")
           [{:place 1, :team-name "Sweater Weather",     :team-id "team-yasy1hnnku8t"}
            {:place 2, :team-name "Discaffeinated",      :team-id "team-wD1jVxJdmjkZ"}
            {:place 3, :team-name "Spirits of the Game", :team-id "team-Q3H4Pr6eEf3c"}
            {:place 4, :team-name "Huck-O-Lanterns",     :team-id "team-5Qaw8MxNJMAz"}
            {:place 5, :team-name "Headless Horsemen",   :team-id "team-S5hApBgb9pA5"}
            {:place 6, :team-name "Huckleberry Pie",     :team-id "team-vfzgApxUkmEL"}]))))
