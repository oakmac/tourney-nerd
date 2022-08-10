(ns tourney-nerd.advance-event-test
  (:require
    [clojure.test :refer :all]
    [tourney-nerd.advance-event :as tn.advance-event]))

(def games1-before
  {"game-400"
   {:teamA-id "team-1"
    :teamB-id "team-4"
    :scoreA 6
    :scoreB 5
    :status "STATUS_FINAL"}
   "game-401"
   {:teamA-id nil
    :teamB-id "team-6"
    :pending-teamA
    {:type "PENDING_GAME_RESULT"
     :game-id "game-400"
     :result "winner-of"}
    :scoreA 0
    :scoreB 0
    :status "STATUS_SCHEDULED"}
   "game-402"
   {:teamA-id "team-3"
    :teamB-id nil
    :pending-teamB
    {:type "PENDING_GAME_RESULT"
     :game-id "game-400"
     :result "loser-of"}
    :scoreA 0
    :scoreB 0
    :status "STATUS_SCHEDULED"}})

(def games1-after
  {"game-400"
   {:teamA-id "team-1"
    :teamB-id "team-4"
    :scoreA 6
    :scoreB 5
    :status "STATUS_FINAL"}
   "game-401"
   {:teamA-id "team-1"
    :teamB-id "team-6"
    :pending-teamA
    {:type "PENDING_GAME_RESULT"
     :game-id "game-400"
     :result "winner-of"}
    :scoreA 0
    :scoreB 0
    :status "STATUS_SCHEDULED"}
   "game-402"
   {:teamA-id "team-3"
    :teamB-id "team-4"
    :pending-teamB
    {:type "PENDING_GAME_RESULT"
     :game-id "game-400"
     :result "loser-of"}
    :scoreA 0
    :scoreB 0
    :status "STATUS_SCHEDULED"}})

(deftest advance-brackets-test
  (testing "Advance Pending Game Result"
    (is (= (tn.advance-event/advance-event {:games games1-before})
           {:games games1-after}))))
