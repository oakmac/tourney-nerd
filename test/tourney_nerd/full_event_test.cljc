(ns tourney-nerd.full-event-test
  (:require
    [clojure.test :refer [deftest is]]
    [tourney-nerd.event :as tn.event]))

(deftest pool-play-followed-by-single-elim-bracket-test
  (testing "Create Event"
    (let [evt1 (tn.event/create-event "Test Event 1")]
      (is (= (tn.event/get-name evt1) "Test Event 1"))
      (testing "Create Divisions"
        (let [evt2 (-> evt1
                     (tn.divisions/create-division "Division A")
                     (tn.divisions/create-division {:name "Division B"}))]
          (= (map? (tn.divisions/get-divisions evt2)))
          (= (-> evt2 tn.divisions/get-divisions count) 2)
          (= (vector? (tn.divisions/get-sorted-divisions evt2)))
          (= (->> (tn.divisions/get-sorted-divisions evt2)
                  (map :name))
             ["Division A" "Division B"]))))))
          ;; TODO: re-order divisions
          ;; TODO: edit division name
          ;; TODO: delete division

;; TODO: add fields
;; TODO: re-order fields
;; TODO: edit field name
;; TODO: edit field info
;; TODO: delete field

;; TODO: create teams
;; TODO: re-order team seed
;; TODO: rename team
;; TODO: add team captain
;; TODO: add team jersey color
;; TODO: delete team

;; TODO: add game group
;; TODO:
