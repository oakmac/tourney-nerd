(ns tourney-nerd.teams
  (:require
    [clojure.set :as set]
    [malli.core :as malli]
    [tourney-nerd.util.base58 :refer [random-base58]]))

(def team-id-regex
  #"^team-[a-zA-Z0-9]{4,}$")

(defn teams->sorted-by-seed
  "Convert teams into a list ordered by their seed."
  [teams]
  (let [teams (if (map? teams) (vals teams) teams)]
    (assert (sequential? teams) "Non-sequential value for teams passed to teams->sorted-by-seed")
    (sort-by :seed teams)))

(defn team-id
  "returns a fresh team-id"
  []
  (str "team-" (random-base58)))

(def team-schema
  [:map
   [:id [:re team-id-regex]]
   [:division-id [:re #"^division-[a-zA-Z0-9]{4,}$"]]
   [:name [:string {:min 3, :max 100}]]])

(def keys-required-to-create-a-team
  #{:division-id :name})

;; TODO: re-write this using Malli
(defn enough-information-to-create-a-team?
  [opts]
  (and (map? opts)
       (set/subset? keys-required-to-create-a-team (-> opts keys set))))

(defn create-team
  "Creates a single team"
  [opts]
  {:pre [(enough-information-to-create-a-team? opts)]
   :post [(malli/validate team-schema %)]}
  (let [new-id (team-id)]
    (merge
      {:id new-id}
      opts)))

(defn create-n-teams
  "Returns a Sequence of N teams."
  [division-id num-teams]
  (map-indexed
    (fn [idx n]
      (create-team {:division-id division-id
                    :name (str "Team " (inc idx))}))
    (range 0 num-teams)))
