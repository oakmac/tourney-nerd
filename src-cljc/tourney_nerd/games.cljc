(ns tourney-nerd.games
  (:require
    [clojure.set :as set]
    [malli.core :as malli]
    [tourney-nerd.divisions :refer [division-id-regex]]
    [tourney-nerd.teams :refer [team-id-regex]]
    [tourney-nerd.util.base58 :refer [random-base58]]))

;; TODO: move these to their respective namespaces
(def group-id-regex #"^group-[a-zA-Z0-9]{4,}$")
(def field-id-regex #"^field-[a-zA-Z0-9]{4,}$")
(def timeslot-id-regex #"^timeslot-[a-zA-Z0-9]{4,}$")

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
    forfeit-status})

;; -----------------------------------------------------------------------------
;; Game Creation

(def game-id-regex
  #"^game-[a-zA-Z0-9]{4,}$")

(defn game-id
  "returns a fresh game-id"
  []
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
    [:status [:enum scheduled-status in-progress-status aborted-status canceled-status forfeit-status]]]])
   ; [:fn (fn [{:keys [status]}])]])

(defn create-game
  "creates a new Game"
  [opts]
  {:post [(malli/validate game-schema %)]}
  (let [new-id (game-id)]
    (merge
      {:id new-id
       :status scheduled-status
       :scoreA 0
       :scoreB 0
       :name nil
       :description nil}
      opts)))

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
