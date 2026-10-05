(ns com.oakmac.tourney-nerd.games
  (:require
   [clojure.set :as set]
   [clojure.string :as str]
   [com.oakmac.tourney-nerd.util.ids :as util.ids]))

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
(defn valid-game?
  "Is g a well-formed Game? Checks every field, so a Game with pending teams
  (nil teamA-id / teamB-id) is not valid. See game? to recognize Games inside an Event."
  [g]
  (and (map? g)
       (util.ids/game-id? (:id g))
       (util.ids/division-id? (:division-id g))
       (util.ids/group-id? (:group-id g))
       (util.ids/team-id? (:teamA-id g))
       (util.ids/team-id? (:teamB-id g))
       (util.ids/timeslot-id? (:timeslot-id g))
       (util.ids/field-id? (:field-id g))
       ;; TODO: validate :name (string, 3-100 chars)
       (contains? game-statuses (:status g))
       ;; a team cannot play itself
       (not= (:teamA-id g) (:teamB-id g))))

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
  "Does g look like a Game? A loose check used to recognize Games when walking an
  Event; it accepts Games with pending teams. See valid-game? for a strict check."
  [g]
  (and
    (map? g)
    (looks-like-a-game-id? (:id g))
    (contains? game-statuses (:status g))
    (set/subset? #{:id :status :teamA-id :teamB-id} (set (keys g)))))

(defn create-game
  "creates a new Game"
  [opts]
  {:post [(valid-game? %)]}
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
;; TODO: mark this function as deprecated
(defn game-finished? [game]
  (contains? #{final-status "finished"} (:status game)))

(defn final?
  "Has this game been played?"
  [game]
  (= final-status (:status game)))

(defn games->counts
  "Returns how many games have been played. games may be a map or a list.
    {:total 30, :final 9, :remaining 21}
  :final is the number of STATUS_FINAL games; :remaining is every other status
  (scheduled, in progress, canceled, etc)."
  [games]
  (let [games (if (map? games) (vals games) games)
        total (count games)
        final (count (filter final? games))]
    {:total total
     :final final
     :remaining (- total final)}))

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

(defn game->winning-team-id
  "returns the winning team-id from a game if it is final and one team has scored more points than the other
  nil otherwise (including ties)"
  [{:keys [scoreA scoreB teamA-id teamB-id] :as game}]
  (when (final? game)
    (cond
      (> scoreA scoreB) teamA-id
      (> scoreB scoreA) teamB-id
      :else nil)))

(defn game->losing-team-id
  "returns the losing team-id from a game if it is final and one team has scored less points than the otherwise
  nil otherwise (including ties)"
  [{:keys [scoreA scoreB teamA-id teamB-id] :as game}]
  (when (final? game)
    (cond
      (< scoreA scoreB) teamA-id
      (< scoreB scoreA) teamB-id
      :else nil)))
