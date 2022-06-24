(ns tourney-nerd.tiebreaker-test
  (:require
    [clojure.test :refer [deftest is testing]]
    [tourney-nerd.tiebreaker :refer [sort-results-by-upa-rules]]
    [tourney-nerd.results :refer [games->results]]))

(def six-team-rr-teams
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
(def six-team-rr-games
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

(defn fooooooooo
  []
  (let [xxx (games->results six-team-rr-teams six-team-rr-games)]
    (zipmap (map :team-name xxx)
            (map :record xxx))))

(deftest sort-results-by-upa-rules-test
;; TODO:
;; Example 2.1. A and B are tied for third place at 4-2, and during the
;; tournament, A has beaten B. Then, A gets third place and B gets fourth place. When
;; only two teams are involved, this rule is commonly called "head-to-head."

  (testing "Example 2.2. A, B, and C, are tied for first place; they are all 3-2 after the
six team round-robin. A has beaten both B and C, while B has beaten C. The records
among the three teams only are: A is 2-0, B is 1-1, and C is 0-2. A finishes first,
B finishes second, and C finishes third."
    (is (= (->> (games->results six-team-rr-teams six-team-rr-games)
             (take 3)
             (map :team-name)
             vec)
           ["A" "B" "C"]))))
