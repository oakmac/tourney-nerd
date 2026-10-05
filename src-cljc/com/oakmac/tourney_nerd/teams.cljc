(ns com.oakmac.tourney-nerd.teams
  (:require
   [clojure.set :as set]
   [clojure.string :as str]
   [com.oakmac.tourney-nerd.util :as util]
   [com.oakmac.tourney-nerd.util.ids :as util.ids]))

(defn teams->sorted-by-seed
  "Convert teams into a list ordered by their seed."
  [teams]
  (let [teams (if (map? teams) (vals teams) teams)]
    (assert (sequential? teams) "Non-sequential value for teams passed to teams->sorted-by-seed")
    (sort-by :seed teams)))

(defn valid-team?
  "Is t a well-formed Team? Checks every field, including seed.
  See team? to recognize Teams inside an Event."
  [t]
  (and (map? t)
       (util.ids/team-id? (:id t))
       (util.ids/division-id? (:division-id t))
       (util/string-of-length? (:name t) 3 100)
       (pos-int? (:seed t))))
       ;; TODO: need optional captain + team members information here

(defn- looks-like-a-team-id? [id]
  (and
    (string? id)
    (str/starts-with? id "team-")))

(defn team?
  "Does t look like a Team? A loose check used to recognize Teams when walking an
  Event. See valid-team? for a strict check."
  [t]
  (and
    (map? t)
    (looks-like-a-team-id? (:id t))
    (set/subset? #{:id :name :division-id} (set (keys t)))))

(defn create-team
  "Creates a single team"
  [opts]
  {:post [(valid-team? %)]}
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
