(ns com.oakmac.tourney-nerd.results-test
  (:require
    [clojure.test :refer [deftest is testing]]
    [com.oakmac.tourney-nerd.results :refer [games->results games->sorted-results]]))

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
