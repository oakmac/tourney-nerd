(ns tourney-nerd.round-robin-pool-test
  (:require
    [clojure.test :refer :all]
    [tourney-nerd.round-robin-pool :refer [rr-pool->games-template]]
    [tourney-nerd.upa.round-robin-pools :as upa-pools]))

(def table-3-1-games-template
  [{:field-num 1
    :schedule-num 1
    :teamA-num 1
    :teamB-num 3}
   {:field-num 1
    :schedule-num 2
    :teamA-num 2
    :teamB-num 3}
   {:field-num 1
    :schedule-num 3
    :teamA-num 1
    :teamB-num 2}])

(def table-4-1-games-template
  [{:field-num 1
    :schedule-num 1
    :teamA-num 1
    :teamB-num 3}
   {:field-num 2
    :schedule-num 1
    :teamA-num 2
    :teamB-num 4}

   {:field-num 1
    :schedule-num 2
    :teamA-num 1
    :teamB-num 4}
   {:field-num 2
    :schedule-num 2
    :teamA-num 2
    :teamB-num 3}

   {:field-num 1
    :schedule-num 3
    :teamA-num 1
    :teamB-num 2}
   {:field-num 2
    :schedule-num 3
    :teamA-num 3
    :teamB-num 4}])

(deftest test-create-games-template
  (is (= table-3-1-games-template (rr-pool->games-template upa-pools/table-3-1)))
  (is (= table-4-1-games-template (rr-pool->games-template upa-pools/table-4-1))))
