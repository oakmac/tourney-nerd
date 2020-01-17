(ns tourney-nerd.test-tiebreaker
  (:require
    [clojure.test :refer :all]
    [tourney-nerd.tiebreaker :as tiebreaker]))

(deftest example-test-aaa
  (is (= 1 1)))

(deftest tiebreaker-test-1
  (testing "Tiebreaker Test 1"
    (is (= "tiebreaker!" (tiebreaker/hello)))))

; (def example-2_1-tournament
;   {:title "Example 2.1 Tournament"
;    :teams
;    {"team-a" {:}}
;    "game-1000" {:}})

(deftest example-2_1
  (testing "Example 2.1. A and B are tied for third place at 4-2, and during the
         tournament, A has beaten B. Then, A gets third place and B gets fourth place. When
         only two teams are involved, this rule is commonly called 'head-to-head.'"))

(deftest example-2_2
  (testing "Example 2.2. A, B, and C, are tied for first place; they are all 3-2 after the
            six team round-robin. A has beaten both B and C, while B has beaten C. The records
            among the three teams only are: A is 2-0, B is 1-1, and C is 0-2. A finishes first,
            B finishes second, and C finishes third."))
