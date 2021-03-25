(ns tourney-nerd.games
  (:require
    [clojure.set :as set]
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
    forfeit-status})

;; -----------------------------------------------------------------------------
;; Game Creation

(defn game-id
  "returns a fresh game-id"
  []
  (str "game-" (random-base58)))

;; NOTE: division-id is downstream from team-id, but I think it's fine to require it for Games
;; makes many operations easier
(def key-required-to-create-a-game
  #{:division-id :game-group-id :teamA-id :teamB-id :field-id :timeslot-id})

(defn enough-information-to-create-a-game?
  [opts]
  (and (map? opts)
       (set/subset? key-required-to-create-a-game (-> opts keys set))))

(defn create-game
  "creates a new Game"
  [opts]
  {:pre [(enough-information-to-create-a-game? opts)]}
  (let [new-id (game-id)]
    (merge
      {:id new-id
       :status scheduled-status
       :scoreA 0
       :scoreB 0
       :name nil
       :description nil}
      opts)))

(def ex-opts1
  {:division-id "division-23823232"
   :teamA-id "team-jdfdfdfdf"
   :teamB-id "team-sdfasdf23434"
   :field-id "field-34343"
   :schedule-id "time-2823232"
   :game-group-id "group-23238873"})

;; -----------------------------------------------------------------------------
;; Create Games from Round Robin Pools

;; TODO: move to util namespace
(defn str->int
  "convert s to an Integer"
  [s]
  #?(:clj  (java.lang.Integer/parseInt s)
     :cljs (js/parseInt s 10)))






;; FIXME: this needs tests
(defn create-games-from-pool
  [division-id teams fields rounds rr-pool]
  (let [games (atom [])
        round-idx (atom 0)]
    (doseq [row rr-pool]
      (let [field-idx (atom 0)]
        (doseq [matchup-str row]
          (let [[teamA-num teamB-num] (str/split matchup-str #"v")
                new-game (games/create-game {:division-id division-id
                                             :game-group-id "FIXME"
                                             :teamA-id (nth teams (dec (str->int teamA-num)))
                                             :teamB-id (nth teams (dec (str->int teamB-num)))
                                             :field-id (nth fields @field-idx)
                                             :timeslot-id (nth rounds @round-idx)})]
            (swap! games conj new-game))
          (swap! field-idx inc)))
      (swap! round-idx inc))
    @games))
