(ns com.oakmac.tourney-nerd.groups
  (:require
   [com.oakmac.tourney-nerd.games :as tn.games]
   [com.oakmac.tourney-nerd.order :refer [ensure-items-order]]
   [com.oakmac.tourney-nerd.util :as util]))

(defn get-group-by-id
  "Returns the Group with group-id from an Event, nil otherwise.
  The Event may be keyed by string or keyword."
  [event group-id]
  (util/get-by-id (or (:groups event) (get event "groups")) group-id))

(defn get-all-games-for-group
  "returns a map of all the games for a given group-id"
  [event group-id]
  (let [group-id-str (if (keyword? group-id) (name group-id) (str group-id))]
    (reduce
      (fn [games [game-id game]]
        (if (= group-id-str (:group-id game))
          (assoc games game-id game)
          games))
      {}
      (:games event))))

(defn get-teams-for-group
  "returns a map of all the teams for a given group-id"
  [event group-id]
  (let [games (get-all-games-for-group event group-id)
        team-ids (reduce
                   (fn [ids {:keys [teamA-id teamB-id] :as _game}]
                     (conj ids teamA-id teamB-id))
                   #{}
                   (vals games))
        ;; ensure that the teams map has string keys
        teams (zipmap (map name (keys (:teams event)))
                      (vals (:teams event)))]
    (select-keys teams team-ids)))

(defn ensure-groups-order
  "Ensures that all groups have a sequential :order field per division"
  [groups]
  (let [groups-by-division (group-by :division-id (vals groups))
        groups-with-order (map
                            (fn [[_division-id groups]]
                              (ensure-items-order groups))
                            groups-by-division)
        groups-coll (flatten groups-with-order)]
    (zipmap (map #(-> % :id keyword) groups-coll)
            groups-coll)))

(defn group->game-counts
  "Returns how many of a Group's games have been played. See games->counts.
    {:total 30, :final 9, :remaining 21}"
  [event group-id]
  (tn.games/games->counts (get-all-games-for-group event group-id)))

(defn all-games-final?
  "Have all of the games in this group been played? ie: are they all STATUS_FINAL?"
  [event group-id]
  (let [games (get-all-games-for-group event group-id)]
    (every? tn.games/final? (vals games))))

;; TODO: we should have an "group integrity" function that ensures bracket groups have result placements
