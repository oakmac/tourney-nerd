(ns tourney-nerd.advance-event-test
  (:require
    [clojure.test :refer [deftest is testing]]
    [tourney-nerd.advance-event :as tn.advance-event]
    [tourney-nerd.test-util :refer [load-test-resource-json-file]]))

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
           {:games games1-after}))
    (is (= (tn.advance-event/advance-event {:games games1-after})
           {:games games1-after}))))

;; Three teams here: team-1, team-2, team-3
;; Games: 1v2, 1v3, 2v3
;; Results:
;; team-1: 2-0
;; team-2: 1-1
;; team-3: 0-2
(def event-group-advance-before
  {:divisions
   {"division-1"
    {:id "division-1"
     :name "Open Division"}}

   :groups
   {"group-1"
    {:id "group-1"
     :name "Pool Play 1"
     :division-id "division-1"}
    "group-2"
    {:id "group-2"
     :name "Pool play 2"
     :division-id "division-1"}
    "group-3"
    {:id "group-3"
     :name "Finals"
     :division-id "division-1"}}

   :teams
   {"team-1" {:id "team-1", :name "Team 1"}
    "team-2" {:id "team-2", :name "Team 2"}
    "team-3" {:id "team-3", :name "Team 3"}}

   :games
   {"game-100"
    {:teamA-id "team-1"
     :teamB-id "team-2"
     :scoreA 10
     :scoreB 5
     :status "STATUS_FINAL"
     :group-id "group-1"}
    "game-101"
    {:teamA-id "team-1"
     :teamB-id "team-3"
     :scoreA 10
     :scoreB 1
     :status "STATUS_FINAL"
     :group-id "group-1"}
    "game-102"
    {:teamA-id "team-2"
     :teamB-id "team-3"
     :scoreA 5
     :scoreB 1
     :status "STATUS_FINAL"
     :group-id "group-1"}

    "game-200"
    {:teamA-id nil
     :pending-teamA
     {:type "PENDING_GROUP_RESULT"
      :group-id "group-1"
      :place 1}
     :teamB-id nil
     :pending-teamB
     {:type "PENDING_GROUP_RESULT"
      :group-id "group-1"
      :place 2}
     :scoreA 0
     :scoreB 0
     :status "STATUS_SCHEDULED"
     :group-id "group-3"}
    "game-201"
    {:teamA-id nil
     :pending-teamA
     {:type "PENDING_GROUP_RESULT"
      :group-id "group-2" ;; a group we do not have games from
      :place 1}
     :teamB-id nil
     :pending-teamB
     {:type "PENDING_GROUP_RESULT"
      :group-id "group-1"
      :place 3}
     :scoreA 0
     :scoreB 0
     :status "STATUS_SCHEDULED"
     :group-id "group-3"}}})

(def event-group-advance-after
  {:divisions
   {"division-1"
    {:id "division-1"
     :name "Open Division"}}

   :groups
   {"group-1"
    {:id "group-1"
     :name "Pool Play 1"
     :division-id "division-1"}
    "group-2"
    {:id "group-2"
     :name "Pool play 2"
     :division-id "division-1"}
    "group-3"
    {:id "group-3"
     :name "Finals"
     :division-id "division-1"}}

   :teams
   {"team-1" {:id "team-1", :name "Team 1"}
    "team-2" {:id "team-2", :name "Team 2"}
    "team-3" {:id "team-3", :name "Team 3"}}

   :games
   {"game-100"
    {:teamA-id "team-1"
     :teamB-id "team-2"
     :scoreA 10
     :scoreB 5
     :status "STATUS_FINAL"
     :group-id "group-1"}
    "game-101"
    {:teamA-id "team-1"
     :teamB-id "team-3"
     :scoreA 10
     :scoreB 1
     :status "STATUS_FINAL"
     :group-id "group-1"}
    "game-102"
    {:teamA-id "team-2"
     :teamB-id "team-3"
     :scoreA 5
     :scoreB 1
     :status "STATUS_FINAL"
     :group-id "group-1"}

    "game-200"
    {:teamA-id "team-1"
     :pending-teamA
     {:type "PENDING_GROUP_RESULT"
      :group-id "group-1"
      :place 1}
     :teamB-id "team-2"
     :pending-teamB
     {:type "PENDING_GROUP_RESULT"
      :group-id "group-1"
      :place 2}
     :scoreA 0
     :scoreB 0
     :status "STATUS_SCHEDULED"
     :group-id "group-3"}
    "game-201"
    {:teamA-id nil
     :pending-teamA
     {:type "PENDING_GROUP_RESULT"
      :group-id "group-2" ;; a group we do not have games from
      :place 1}
     :teamB-id "team-3"
     :pending-teamB
     {:type "PENDING_GROUP_RESULT"
      :group-id "group-1"
      :place 3}
     :scoreA 0
     :scoreB 0
     :status "STATUS_SCHEDULED"
     :group-id "group-3"}}})

(deftest advance-groups-test
  (testing "Advance Pending games based on group result"
    (is (= (tn.advance-event/advance-event event-group-advance-before)
           event-group-advance-after))
    (is (= (tn.advance-event/advance-event event-group-advance-after)
           event-group-advance-after)))
  (testing "real-world league advancement example"
    (let [before (load-test-resource-json-file "woodlands-fall-league.before.json")
          after (load-test-resource-json-file "woodlands-fall-league.after.json")]
      (is (= (tn.advance-event/advance-event before)
             after)))))

