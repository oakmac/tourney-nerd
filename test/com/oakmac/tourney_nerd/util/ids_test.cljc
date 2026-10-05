(ns com.oakmac.tourney-nerd.util.ids-test
  (:require
   [clojure.test :refer [deftest is]]
   [com.oakmac.tourney-nerd.util.ids :as ids]))

(deftest id-predicates-test
  (is (true? (ids/division-id? "division-KmbM3AMx8xe3")))
  (is (true? (ids/field-id? "field-tTzY73PenZeT")))
  (is (true? (ids/game-id? "game-cMEeozzWc3s3")))
  (is (true? (ids/group-id? "group-4TCxLwc2rKqP")))
  (is (true? (ids/team-id? "team-aaaaaaaa")))
  (is (true? (ids/timeslot-id? "timeslot-8zNxrhmWeNrR")))

  (is (false? (ids/team-id? "game-cMEeozzWc3s3")) "wrong type")
  (is (false? (ids/team-id? "team-abc")) "too short")
  (is (false? (ids/team-id? "team-abc_def")) "not alphanumeric")
  (is (false? (ids/team-id? :team-aaaaaaaa)) "not a string")
  (is (false? (ids/team-id? nil))))

(deftest create-id-test
  (is (true? (ids/division-id? (ids/create-division-id))))
  (is (true? (ids/field-id? (ids/create-field-id))))
  (is (true? (ids/game-id? (ids/create-game-id))))
  (is (true? (ids/group-id? (ids/create-group-id))))
  (is (true? (ids/team-id? (ids/create-team-id))))
  (is (true? (ids/timeslot-id? (ids/create-timeslot-id)))))
