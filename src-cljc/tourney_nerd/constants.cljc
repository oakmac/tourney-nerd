(ns tourney-nerd.constants
  "Things that do not change.")

;; -----------------------------------------------------------------------------
;; Game Statuses

(def scheduled-status "STATUS_SCHEDULED")
(def in-progress-status "STATUS_IN_PROGRESS")
;; TODO: aborted?
;; TODO: canceled?
(def final-status "STATUS_FINAL")
(def game-statuses #{scheduled-status in-progress-status final-status})
