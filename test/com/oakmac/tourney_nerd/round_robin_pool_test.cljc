(ns com.oakmac.tourney-nerd.round-robin-pool-test
  (:require
    [clojure.test :refer [deftest is]]
    [com.oakmac.tourney-nerd.round-robin-pool :refer [rr-template->games-template]]
    [com.oakmac.tourney-nerd.upa.round-robin-pools :as upa-pools]))

(def table-3-1-games-template
  [{:teamA-idx 0, :teamB-idx 2, :field-idx 0, :timeslot-idx 0}
   {:teamA-idx 1, :teamB-idx 2, :field-idx 0, :timeslot-idx 1}
   {:teamA-idx 0, :teamB-idx 1, :field-idx 0, :timeslot-idx 2}])

(deftest rr-template-conversion
  (is (= (rr-template->games-template upa-pools/table-3-1) table-3-1-games-template)))

; (deftest create-round-robin-pool-games
;   (id (= ex-games1 (create-games-from-pool-template))))
