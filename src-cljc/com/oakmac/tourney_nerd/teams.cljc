(ns com.oakmac.tourney-nerd.teams
  (:require
   [clojure.set :as set]
   [clojure.string :as str]
   [com.oakmac.tourney-nerd.util.ids :as util.ids]
   [malli.core :as malli]))

(defn teams->sorted-by-seed
  "Convert teams into a list ordered by their seed."
  [teams]
  (let [teams (if (map? teams) (vals teams) teams)]
    (assert (sequential? teams) "Non-sequential value for teams passed to teams->sorted-by-seed")
    (sort-by :seed teams)))

(def team-schema
  [:map
   [:id [:re util.ids/team-id-regex]]
   [:division-id [:re util.ids/division-id-regex]]
   [:name [:string {:min 3, :max 100}]]
   [:seed [:int {:min 1}]]])
   ;; TODO: need optional captain + team members information here

(defn- looks-like-a-team-id? [id]
  (and
    (string? id)
    (str/starts-with? id "team-")))

(defn team?
  "Is t a Team?"
  [t]
  (and
    (map? t)
    (looks-like-a-team-id? (:id t))
    (set/subset? #{:id :name :division-id} (set (keys t)))))

;; TODO: we should be able to use Malli for this
; (def team? (malli/validator team-schema))

(defn create-team
  "Creates a single team"
  [opts]
  {:post [(malli/validate team-schema %)]}
  (let [new-id (util.ids/create-team-id)]
    (merge
      {:id new-id}
      opts)))

(defn create-n-teams
  "returns a map of N Teams; used for Event Creation"
  [division-id num-teams]
  (let [teams-list (map-indexed
                     (fn [idx _n]
                       (create-team {:division-id division-id
                                     :name (str "Team " (inc idx))
                                     :seed (inc idx)}))
                     (range 0 num-teams))]
    (zipmap (map #(-> % :id keyword) teams-list)
            teams-list)))

(defn reset-team
  "Resets the values of a Team for a new Event."
  [{:keys [seed] :as team}]
  (assoc team :name (str "Team " seed)))
  ;; TODO: clear out captain information here

(defn get-team-by-id
  "Returns a team with team-id, nil otherwise"
  [event team-id]
  (or
    (get-in event [:teams (keyword team-id)])
    (get-in event ["teams" (str team-id)])))
