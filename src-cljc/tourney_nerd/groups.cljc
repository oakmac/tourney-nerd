(ns tourney-nerd.groups
  (:require
    [tourney-nerd.util.base58 :refer [random-base58]]))

(def group-id-regex #"^group-[a-zA-Z0-9]{4,}$")

(defn random-group-id []
  (str "group-" (random-base58)))

(defn get-all-games-for-group
  "returns a map of all the games for a given group-id"
  [event group-id]
  (reduce
    (fn [games [game-id game]]
      (if (= group-id (:group-id game))
        (assoc games game-id game)
        games))
    {}
    (:games event)))

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
