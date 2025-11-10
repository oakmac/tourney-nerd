(ns com.oakmac.tourney-nerd.games
  (:require
   [clojure.set :as set]
   [clojure.string :as str]
   [com.oakmac.tourney-nerd.util.ids :as util.ids]
   [malli.core :as malli]))

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

;; NOTE: division-id is downstream from team-id, but I think it's fine to require it for Games
;; makes many operations easier
(def game-schema
  [:and
   [:map
    [:id [:re util.ids/game-id-regex]]
    [:division-id [:re util.ids/division-id-regex]]
    [:group-id [:re util.ids/group-id-regex]]
    [:teamA-id [:re util.ids/team-id-regex]]
    [:teamB-id [:re util.ids/team-id-regex]]
    [:timeslot-id [:re util.ids/timeslot-id-regex]]
    [:field-id [:re util.ids/field-id-regex]]
    ; [:name [:string {:min 3, :max 100}]]
    [:status [:enum scheduled-status in-progress-status aborted-status
                    canceled-status final-status forfeit-status]]]
   [:fn (fn [{:keys [teamA-id teamB-id]}]
          (not= teamA-id teamB-id))]])

(defn- looks-like-a-game-id? [id]
  (and
    (string? id)
    (str/starts-with? id "game-")))

(defn game->id [game]
  (cond
    (looks-like-a-game-id? (:id game))
    (:id game)

    (looks-like-a-game-id? (:game-id game))
    (:game-id game)

    :else
    (throw (ex-info "Unable to get game-id from game:" game))))

(defn game?
  "Is g a Game?"
  [g]
  (and
    (map? g)
    (looks-like-a-game-id? (:id g))
    (contains? game-statuses (:status g))
    (set/subset? #{:id :status :teamA-id :teamB-id} (set (keys g)))))

;; TODO: we should be able to use Malli for this
; (def game? (malli/validator game-schema))

(defn create-game
  "creates a new Game"
  [opts]
  {:post [(malli/validate game-schema %)]}
  (merge
    {:id (util.ids/create-game-id)
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
  (cond-> g
    true (assoc :scoreA 0
                :scoreB 0
                :status scheduled-status)

    (map? (:pending-teamA g))
    (assoc :teamA-id nil)

    (map? (:pending-teamB g))
    (assoc :teamB-id nil)))

;; NOTE: "finished" is legacy here
;; TODO: mark deprecated
(defn game-finished? [game]
  (boolean
    (or (= final-status (:status game))
        (= "finished" (:status game)))))

(defn final?
  "Has this game been played?"
  [game]
  (= final-status (:status game)))

(defn games->games-list
  "Converts games into a list or throws if unable to do so"
  [games]
  (cond
    (sequential? games) games

    (map? games) (reduce
                   (fn [acc [game-id-key game]]
                     (let [game-id-key-str (str game-id-key)
                           game-id2 (cond
                                      (looks-like-a-game-id? game-id-key-str) game-id-key-str
                                      (looks-like-a-game-id? (:id game)) (:id game)
                                      (looks-like-a-game-id? (:game-id game)) (:game-id game)
                                      :else (throw (ex-info "Game does not have an id:" game)))]
                       (conj acc (assoc game :id game-id2))))
                   []
                   games)

    :else (throw (ex-info "Unable to convert games into a list:" games))))

(defn get-games-played-between-two-teams
  "Returns a hash map of the games played between two teams.

  games can either be a list or a map"
  [games teamA-id teamB-id]
  (let [teams-id-set (set [teamA-id teamB-id])]
    ;; this reduce is a filter
    (reduce
      (fn [filtered-games game]
        (if (= teams-id-set (set [(:teamA-id game) (:teamB-id game)]))
          (assoc filtered-games (game->id game) game)
          filtered-games))
      {}
      (games->games-list games))))

;; FIXME: easy candidate for unit tests
;; FIXME: what to return when tied?
(defn game->winning-team-id
  "returns the winning team-id from a game if it is final, nil otherwise"
  [{:keys [scoreA scoreB teamA-id teamB-id] :as game}]
  (when (final? game)
    (cond
      (> scoreA scoreB) teamA-id
      (> scoreB scoreA) teamB-id
      :else nil)))

(defn game->losing-team-id
  "returns the losing team-id from a game if it is final, nil otherwise"
  [{:keys [scoreA scoreB teamA-id teamB-id] :as game}]
  (when (final? game)
    (cond
      (< scoreA scoreB) teamA-id
      (< scoreB scoreA) teamB-id
      :else nil)))
