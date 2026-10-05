(ns com.oakmac.tourney-nerd.groups-test
  (:require
   [clojure.test :refer [deftest is]]
   [com.oakmac.tourney-nerd.groups :as groups]
   [com.oakmac.tourney-nerd.test-util :refer [load-test-resource-json-file]]))

(def woodlands-fall-league-2025 (load-test-resource-json-file "2025-woodlands-fall-league.json"))

(deftest get-group-by-id-test
  (is (= "Play-offs" (:name (groups/get-group-by-id woodlands-fall-league-2025 "group-94pXoxYJWzBL"))))
  (is (= "Play-offs" (:name (groups/get-group-by-id woodlands-fall-league-2025 :group-94pXoxYJWzBL))))
  (is (nil? (groups/get-group-by-id woodlands-fall-league-2025 "group-does-not-exist")))
  (is (= "Pool A" (:name (groups/get-group-by-id {"groups" {"group-aaaa" {:name "Pool A"}}} "group-aaaa")))
      "string-keyed event"))
