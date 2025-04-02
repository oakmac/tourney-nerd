(ns com.oakmac.tourney-nerd.events-test
  (:require
   [clojure.test :refer [deftest is]]
   [com.oakmac.tourney-nerd.events :as events]
   [com.oakmac.tourney-nerd.test-util :refer [load-test-resource-edn-file]]))

(def unordered-event (load-test-resource-edn-file "unordered-event.edn"))
(def ordered-event (load-test-resource-edn-file "ordered-event.edn"))

(deftest ensure-order-test
  (is (= (events/ensure-order unordered-event)
         ordered-event)))

;; TODO: finish this "let's play a league" series of API usage + tests

;; FIXME: all teams within the same Division must have unique names
;; but two teams in different divisions can have different names

; (let [event (event/create-event {:title "Woodlands Spring League"})])

;; (api/create-division event {:name "Open"})
;; (api/create-division event {:name "Mixed"})
;; (api/get-divisions event) ;; count should be 2
;; (api/remove-division event "<division-id-or-name>")

;; (api/create-n-teams "<division-id>" 6)
;; (api/get-teams event "<division-id>") ;; count should be 6
;; (api/get-team event "<team-id-or-name") ;; returns two teams if they have the same name?

;; This is a 6-week league. Every team will play twice in round-robin pool play.
;; Then finals will be a single-elimination bracket.
;; (api/create-game-group event "<division-id>" {:name "Pool Play"
;;                                               :type "round-robin-pool"})
;; (api/create-game-group event "<division-id>" {:name "Play-Offs"
;;                                               :type "single-elimination-bracket"})

;; (api/get-games-for-group event "<group-id-or-name>")

;; FIXME: schedule / timeslot
;; (api/create-timeslot event "Week 1, Game 2" "2025-03-20 17:00")
;; FIXME: edit-timeslot
;; FIXME: remove-timeslot

;; FIXME: fields CRUD

;; FIXME: we should put section locks into tourney-nerd
;; (api/lock-teams event)
;; (api/unlock-teams event)

;; FIXME: report scores
;; (api/edit-game "<game-id" {:teamA-score 12
;;                            :teamB-score 8
;;                            :status "final"})

;; (api/report-final-score "<game-id" 12 8)

;; Are all of the games in the Pool Play group finished?
;; (api/are-group-games-final? event "<group-id-or-name>")
;; (api/results-for-group event "<group-id-or-name>")

;; (api/results-for-division event "<division-id-or-name>")
;; (api/results-for-team event "<team-id-or-name>")
