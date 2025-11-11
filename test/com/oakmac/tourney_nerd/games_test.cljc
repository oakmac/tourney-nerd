(ns com.oakmac.tourney-nerd.games-test
  (:require
   [clojure.test :refer [deftest is testing]]
   [com.oakmac.tourney-nerd.games :as g]
   [com.oakmac.tourney-nerd.test-util :refer [load-test-resource-json-file]]))

(def example-game-with-pending
  {:division-id "division-KmbM3AMx8xe3"
   :field-id "field-tTzY73PenZeT"
   :game-id "game-cMEeozzWc3s3"
   :group-id "group-4TCxLwc2rKqP"
   :id "game-cMEeozzWc3s3"
   :name "Finals Round 1: 6th vs 3rd"
   :pending-teamA
   {:type "PENDING_GROUP_RESULT"
    :place 6
    :group-id "group-DDqCeuYpvcXP"}
   :pending-teamB
   {:type "PENDING_GROUP_RESULT"
    :place 3
    :group-id "group-DDqCeuYpvcXP"}
   :scoreA 0
   :scoreB 0
   :status "STATUS_SCHEDULED"
   :teamA-id "team-aaaaaaaa"
   :teamA-type "TEAM_TYPE_DIRECT_TEAM"
   :teamB-id "team-bbbbbbbb"
   :teamB-type "TEAM_TYPE_DIRECT_TEAM"
   :timeslot-id "timeslot-8zNxrhmWeNrR"})

(def example-game-without-pending
  {:description nil
   :division-id "division-XvxxWwQ2Ktgp"
   :field-id "field-GLjEHw8ZrAXN"
   :game-id "game-dAUgrXCHtadx"
   :group-id "group-Xa2hNJmGNKJg"
   :id "game-dAUgrXCHtadx"
   :name "Pool A: 4v2"
   :scoreA 0
   :scoreB 0
   :status "STATUS_SCHEDULED"
   :teamA-id "team-BWYTkqoUCBEz"
   :teamB-id "team-3aiSeihwLnom"
   :timeslot-id "timeslot-PvznhYtikfQf"})

(deftest reset-game-test
  (testing "reset-game should clear any pending teams"
    (let [reset-example-game-with-pending (g/reset-game example-game-with-pending)]
      (is (string? (:teamA-id example-game-with-pending)))
      (is (string? (:teamB-id example-game-with-pending)))
      (is (map? (:pending-teamA example-game-with-pending)))
      (is (map? (:pending-teamB example-game-with-pending)))
      (is (nil? (:teamA-id reset-example-game-with-pending)))
      (is (nil? (:teamB-id reset-example-game-with-pending)))))
  (testing "reset-game should not clear teams when there is no pending game logic"
    (let [reset-example-game-without-pending (g/reset-game example-game-without-pending)]
      (is (string? (:teamA-id example-game-without-pending)))
      (is (string? (:teamB-id example-game-without-pending)))
      (is (nil? (:pending-teamA example-game-without-pending)))
      (is (nil? (:pending-teamB example-game-without-pending)))
      (is (= (:teamA-id example-game-without-pending) (:teamA-id reset-example-game-without-pending)))
      (is (= (:teamB-id example-game-without-pending) (:teamB-id reset-example-game-without-pending))))))

(def woodlands-spring-league (load-test-resource-json-file "2025-woodlands-spring-league.json"))

(deftest game-predicate-test
  (is (true? (g/game? example-game-with-pending)))
  (is (true? (g/game? example-game-without-pending)))
  (is (true? (g/game? (get-in woodlands-spring-league [:games :game-kMmW8tkgPLL7]))))
  (is (true? (g/game? (get-in woodlands-spring-league [:games :game-MBKEoMoKu9Dn]))))
  (is (false? (g/game? nil)))
  (is (false? (g/game? (get-in woodlands-spring-league [:schedule :timeslot-jW4V3zqe1gxh]))))
  (is (false? (g/game? (get-in woodlands-spring-league [:teams :team-3RN4HUBjbEmb])))))

(deftest get-games-played-between-two-teams-test
  (is (= (->> (g/get-games-played-between-two-teams (:games woodlands-spring-league) "team-claritinclear" "team-trophyhusbands")
           vals
           (map :id)
           set)
         #{"game-szPSutCqDEgy" "game-awRtLQzz37UR"}))
  (is (= (g/get-games-played-between-two-teams (:games woodlands-spring-league) "team-claritinclear" "team-does_not_exist")
         {})))

(deftest game-winners-losers-test
  (is (= "team-qNjZiFHLnFDU" (g/game->winning-team-id (get-in woodlands-spring-league [:games :game-d7DcDDjqchL9]))))
  (is (= "team-5GBtxw9d9DUg" (g/game->losing-team-id (get-in woodlands-spring-league [:games :game-d7DcDDjqchL9]))))
  (is (= "team-v4rScUBSYNRg" (g/game->winning-team-id (get-in woodlands-spring-league [:games :game-b85VjQHbVQXT]))))
  (is (= "team-claritinclear" (g/game->losing-team-id (get-in woodlands-spring-league [:games :game-b85VjQHbVQXT]))))
  (is (nil? (g/game->winning-team-id (get-in woodlands-spring-league [:games :game-gG79crxka8NU]))))
  (is (nil? (g/game->losing-team-id (get-in woodlands-spring-league [:games :game-gG79crxka8NU])))))
