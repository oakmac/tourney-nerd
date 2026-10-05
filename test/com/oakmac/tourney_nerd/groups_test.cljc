(ns com.oakmac.tourney-nerd.groups-test
  (:require
   [clojure.test :refer [deftest is]]
   [com.oakmac.tourney-nerd.groups :as groups]
   [com.oakmac.tourney-nerd.test-util :refer [load-test-resource-json-file]]))

(def woodlands-fall-league-2025 (load-test-resource-json-file "2025-woodlands-fall-league.json"))
(def woodlands-spring-league-2025 (load-test-resource-json-file "2025-woodlands-spring-league.json"))
(def woodlands-charity-hat-2025 (load-test-resource-json-file "2025-woodlands-charity-hat.json"))

(deftest group->game-counts-test
  (is (= {:total 30, :final 24, :remaining 6} (groups/group->game-counts woodlands-spring-league-2025 "group-eyrwqfahB1ZX"))
      "spring league round robin: 4 of 5 weeks played")
  (is (= {:total 7, :final 0, :remaining 7} (groups/group->game-counts woodlands-spring-league-2025 "group-gEuBKMfzuNSG"))
      "spring league play-offs have not started")
  (is (= {:total 4, :final 3, :remaining 1} (groups/group->game-counts woodlands-charity-hat-2025 "group-7Rxv6hVzEiXs"))
      "charity hat championship bracket: the 3rd place game was never played")
  (is (= {:total 7, :final 7, :remaining 0} (groups/group->game-counts woodlands-fall-league-2025 :group-94pXoxYJWzBL))
      "fall league play-offs are complete; group-id may be a keyword")
  (is (= {:total 0, :final 0, :remaining 0} (groups/group->game-counts woodlands-fall-league-2025 "group-does-not-exist"))))

(deftest get-group-by-id-test
  (is (= "Play-offs" (:name (groups/get-group-by-id woodlands-fall-league-2025 "group-94pXoxYJWzBL"))))
  (is (= "Play-offs" (:name (groups/get-group-by-id woodlands-fall-league-2025 :group-94pXoxYJWzBL))))
  (is (nil? (groups/get-group-by-id woodlands-fall-league-2025 "group-does-not-exist")))
  (is (= "Pool A" (:name (groups/get-group-by-id {"groups" {"group-aaaa" {:name "Pool A"}}} "group-aaaa")))
      "string-keyed event"))
