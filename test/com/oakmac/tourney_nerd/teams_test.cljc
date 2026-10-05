(ns com.oakmac.tourney-nerd.teams-test
  (:require
   [clojure.test :refer [deftest is]]
   [com.oakmac.tourney-nerd.teams :as teams]
   [com.oakmac.tourney-nerd.test-util :refer [load-test-resource-json-file]]))

(def woodlands-fall-league-2025 (load-test-resource-json-file "2025-woodlands-fall-league.json"))

(deftest get-team-by-id-test
  (is (= "Sweater Weather" (:name (teams/get-team-by-id woodlands-fall-league-2025 "team-yasy1hnnku8t"))))
  (is (= "Sweater Weather" (:name (teams/get-team-by-id woodlands-fall-league-2025 :team-yasy1hnnku8t))))
  (is (nil? (teams/get-team-by-id woodlands-fall-league-2025 "team-does-not-exist")))
  (is (= "Zebras" (:name (teams/get-team-by-id {"teams" {"team-aaaa" {:name "Zebras"}}} "team-aaaa")))
      "string-keyed event"))

(deftest create-team-test
  (let [t (teams/create-team {:division-id "division-KmbM3AMx8xe3"
                              :name "Aardvarks"
                              :seed 1})]
    (is (true? (teams/valid-team? t)))
    (is (= "Aardvarks" (:name t))))
  (is (thrown? #?(:clj AssertionError :cljs js/Error)
               (teams/create-team {:division-id "division-KmbM3AMx8xe3"
                                   :name "Aardvarks"}))
      "seed is required")
  (is (thrown? #?(:clj AssertionError :cljs js/Error)
               (teams/create-team {:division-id "division-KmbM3AMx8xe3"
                                   :name "Aardvarks"
                                   :seed 1
                                   :id "team-abc"}))
      "a caller-supplied id must still be a valid team id"))

(deftest create-n-teams-test
  (let [teams-map (teams/create-n-teams "division-KmbM3AMx8xe3" 4)]
    (is (= 4 (count teams-map)))
    (is (every? keyword? (keys teams-map)))
    (is (every? teams/valid-team? (vals teams-map)))
    (is (= [1 2 3 4] (sort (map :seed (vals teams-map)))))))
