(ns com.oakmac.tourney-nerd.schedule-test
  (:require
   [clojure.test :refer [deftest is]]
   [com.oakmac.tourney-nerd.schedule :as schedule]
   [com.oakmac.tourney-nerd.test-util :refer [load-test-resource-json-file]]))

(def woodlands-fall-league-2025 (load-test-resource-json-file "2025-woodlands-fall-league.json"))

(deftest get-timeslot-by-id-test
  (is (schedule/valid-time? (:time (schedule/get-timeslot-by-id woodlands-fall-league-2025 "timeslot-6U9LfNYwhE1K"))))
  (is (= (schedule/get-timeslot-by-id woodlands-fall-league-2025 "timeslot-6U9LfNYwhE1K")
         (schedule/get-timeslot-by-id woodlands-fall-league-2025 :timeslot-6U9LfNYwhE1K)))
  (is (nil? (schedule/get-timeslot-by-id woodlands-fall-league-2025 "timeslot-does-not-exist"))))

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
