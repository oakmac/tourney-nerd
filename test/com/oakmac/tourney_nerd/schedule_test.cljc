(ns com.oakmac.tourney-nerd.schedule-test
  (:require
   [clojure.test :refer [deftest is]]
   [com.oakmac.tourney-nerd.schedule :as schedule]))

(deftest create-timeslot-test
  (let [ts (schedule/create-timeslot "2026-10-04 09:00" "Round 1")]
    (is (true? (schedule/valid-timeslot? ts)))
    (is (= "2026-10-04 09:00" (:time ts)))
    (is (= "Round 1" (:name ts))))
  (is (thrown? #?(:clj AssertionError :cljs js/Error)
               (schedule/create-timeslot "9am" "Round 1"))
      "time must be formatted YYYY-MM-DD HH:MM")
  (is (thrown? #?(:clj AssertionError :cljs js/Error)
               (schedule/create-timeslot "2026-10-04 09:00" nil))
      "name is required"))
