(ns com.oakmac.tourney-nerd.games-test
  (:require
    [clojure.test :refer [deftest is testing]]
    [com.oakmac.tourney-nerd.games :as g]))

(def example-game-with-pending
  {:game-id "game-cMEeozzWc3s3",
   :pending-teamB
   {:type "PENDING_GROUP_RESULT",
    :place 3,
    :group-id "group-DDqCeuYpvcXP"},
   :pending-teamA
   {:type "PENDING_GROUP_RESULT",
    :place 6,
    :group-id "group-DDqCeuYpvcXP"},
   :group-id "group-4TCxLwc2rKqP",
   :teamB-id "team-bbbbbbbb",
   :name "Finals Round 1: 6th vs 3rd",
   :scoreB 0,
   :scoreA 0,
   :teamA-type "TEAM_TYPE_DIRECT_TEAM",
   :teamB-type "TEAM_TYPE_DIRECT_TEAM",
   :timeslot-id "timeslot-8zNxrhmWeNrR",
   :status "STATUS_SCHEDULED",
   :id "game-cMEeozzWc3s3",
   :teamA-id "team-aaaaaaaa",
   :field-id "field-tTzY73PenZeT",
   :division-id "division-KmbM3AMx8xe3"})

(def example-game-without-pending
  {:description nil,
   :division-id "division-XvxxWwQ2Ktgp",
   :field-id "field-GLjEHw8ZrAXN",
   :game-id "game-dAUgrXCHtadx",
   :group-id "group-Xa2hNJmGNKJg",
   :id "game-dAUgrXCHtadx",
   :name "Pool A: 4v2",
   :scoreA 0,
   :scoreB 0,
   :status "STATUS_SCHEDULED",
   :teamA-id "team-BWYTkqoUCBEz",
   :teamB-id "team-3aiSeihwLnom",
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
