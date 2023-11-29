(ns com.oakmac.tourney-nerd.teams
  (:require
    [com.oakmac.tourney-nerd.divisions :as divisions]
    [com.oakmac.tourney-nerd.util.base58 :refer [random-base58]]
    [malli.core :as malli]))

(def team-id-regex
  #"^team-[a-zA-Z0-9]{4,}$")

(defn teams->sorted-by-seed
  "Convert teams into a list ordered by their seed."
  [teams]
  (let [teams (if (map? teams) (vals teams) teams)]
    (assert (sequential? teams) "Non-sequential value for teams passed to teams->sorted-by-seed")
    (sort-by :seed teams)))

(defn random-team-id []
  (str "team-" (random-base58)))

(def team-schema
  [:map
   [:id [:re team-id-regex]]
   [:division-id [:re divisions/division-id-regex]]
   [:name [:string {:min 3, :max 100}]]
   [:seed [:int {:min 1}]]])
   ;; TODO: need optional captain + team members information here

(defn team?
  "Is t a Team?"
  [t]
  (and (map? t)
       (string? (:id t))
       (re-matches team-id-regex (:id t))))

;; TODO: we should be able to use Malli for this
; (def team? (malli/validator team-schema))

(defn create-team
  "Creates a single team"
  [opts]
  {:post [(malli/validate team-schema %)]}
  (let [new-id (random-team-id)]
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
