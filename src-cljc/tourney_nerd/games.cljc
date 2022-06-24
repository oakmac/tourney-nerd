(ns tourney-nerd.games
  (:require
    [clojure.set :as set]
    [malli.core :as malli]
    [tourney-nerd.divisions :refer [division-id-regex]]
    [tourney-nerd.fields :refer [field-id-regex]]
    [tourney-nerd.groups :refer [group-id-regex]]
    [tourney-nerd.schedule :refer [timeslot-id-regex]]
    [tourney-nerd.teams :refer [team-id-regex]]
    [tourney-nerd.util.base58 :refer [random-base58]]))

;; -----------------------------------------------------------------------------
;; Statuses

(def scheduled-status "STATUS_SCHEDULED")
(def in-progress-status "STATUS_IN_PROGRESS")
(def aborted-status "STATUS_ABORTED")
(def canceled-status "STATUS_CANCELED")
(def final-status "STATUS_FINAL")
(def forfeit-status "STATUS_FORFEIT")

(def game-statuses
  #{scheduled-status
    in-progress-status
    aborted-status
    canceled-status
    final-status
    forfeit-status})

;; -----------------------------------------------------------------------------
;; Game Creation

(def game-id-regex
  #"^game-[a-zA-Z0-9]{4,}$")

(defn random-game-id []
  (str "game-" (random-base58)))

;; NOTE: division-id is downstream from team-id, but I think it's fine to require it for Games
;; makes many operations easier
(def game-schema
  [:and
    [:map
      [:id [:re game-id-regex]]
      [:division-id [:re division-id-regex]]
      [:group-id [:re group-id-regex]]
      [:teamA-id [:re team-id-regex]]
      [:teamB-id [:re team-id-regex]]
      [:timeslot-id [:re timeslot-id-regex]]
      [:field-id [:re field-id-regex]]
      ; [:name [:string {:min 3, :max 100}]]
      [:status [:enum scheduled-status in-progress-status aborted-status
                      canceled-status final-status forfeit-status]]]
    [:fn (fn [{:keys [teamA-id teamB-id]}]
           (not= teamA-id teamB-id))]])

(defn game?
  "Is g a Game?"
  [g]
  (and (map? g)
       (string? (:id g))
       (re-matches game-id-regex (:id g))))

;; TODO: we should be able to use Malli for this
; (def game? (malli/validator game-schema))

(defn create-game
  "creates a new Game"
  [opts]
  {:post [(malli/validate game-schema %)]}
  (merge
    {:id (random-game-id)
     :status scheduled-status
     :scoreA 0
     :scoreB 0
     :name nil
     :description nil}
    opts))

(defn create-games-from-template
  "creates games from a Games Template"
  [division-id group-id teams fields rounds games-template]
  (map
    (fn [{:keys [teamA-idx teamB-idx field-idx timeslot-idx]}]
      (create-game {:division-id division-id
                    :group-id group-id
                    :teamA-id (nth teams teamA-idx)
                    :teamB-id (nth teams teamB-idx)
                    :field-id (nth fields field-idx)
                    :timeslot-id (nth rounds timeslot-idx)}))
    games-template))

(defn reset-game
  "Resets the values of a Game for a new Event."
  [g]
  (assoc g :scoreA 0
           :scoreB 0
           :status scheduled-status))

;; NOTE: "finished" is legacy here
(defn game-finished? [game]
  (or (= final-status (:status game))
      (= "finished" (:status game))))
