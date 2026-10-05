(ns com.oakmac.tourney-nerd.teams-test
  (:require
   [clojure.test :refer [deftest is]]
   [com.oakmac.tourney-nerd.teams :as teams]))

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
