(ns tourney-nerd.schedule
  (:require
    [clojure.string :as str]
    [malli.core :as malli]
    [tourney-nerd.util.base58 :refer [random-base58]]))

(def timeslot-id-regex
  #"^timeslot-[a-zA-Z0-9]{4,}$")

(def iso-8601-regex
  #"^[12]\d\d\d-\d\d-\d\d \d\d:\d\d$")

(defn random-timeslot-id []
  (str "timeslot-" (random-base58)))

(def timeslot-schema
  [:map
   [:id [:re timeslot-id-regex]]
   [:time [:re iso-8601-regex]]
   [:name [:string {:min 3, :max 100}]]])
   ;; TODO: need optional description field here

(defn create-timeslot
  "creates a single Timeslot"
  [time name]
  {:post [(malli/validate timeslot-schema %)]}
  {:id (random-timeslot-id)
   :time time
   :name name})

(def ex-template1
  [{:day-idx 0
    :time "{{date}} 09:00"
    :name "Bagels & Bananas"}
   {:day-idx 0
    :time "{{date}} 09:30"
    :name "Pool Play Round 1"}
   {:day-idx 0
    :time "{{date}} 11:30"
    :name "Pool Play Round 2"}
   {:day-idx 0
    :time "{{date}} 13:30"
    :name "Pool Play Round 3"}
   {:day-idx 0
    :time "{{date}} 15:30"
    :name "Pool Play Round 4"}
   {:day-idx 0
    :time "{{date}} 19:00"
    :name "Tournament Party!"}

   {:day-idx 1
    :time "{{date}} 09:00"
    :name "Bagels & Bananas"}
   {:day-idx 1
    :time "{{date}} 09:30"
    :name "Pool Play Round 5"}
   {:day-idx 1
    :time "{{date}} 11:30"
    :name "Quarter-finals"}
   {:day-idx 1
    :time "{{date}} 13:30"
    :name "Semi-finals"}
   {:day-idx 1
    :time "{{date}} 15:30"
    :name "Finals"}])

(defn create-timeslots-from-template
  "creates a Schedule from a template; used for Event Creation"
  [template-timeslots days]
  ;; TODO: sanity-check that the days Vector is long enough here for the day-idx being used
  (reduce
    (fn [new-schedule {:keys [day-idx name time]}]
      (let [date (nth days day-idx (first days))
            new-time (str/replace time "{{date}}" date)
            new-ts (create-timeslot new-time name)]
        (assoc new-schedule (:id new-ts) new-ts)))
    {}
    template-timeslots))

(defn ts->date
  "returns the date part from an ISO 8601 string"
  [ts]
  (subs ts 0 10))

(defn get-days
  "returns a sorted collection of the unique days in a Schedule"
  [schedule]
  (let [timeslots (if (map? schedule) (vals schedule) schedule)]
    (->> timeslots
      (map :time)
      (map ts->date)
      set
      sort)))

(defn schedule->times-set
  "returns a Set of the times on the Schedule"
  [schedule]
  (->> schedule vals (map :time) set))

(defn timeslot-on-date?
  "Does this timeslot occur on this date?"
  [timeslot date]
  (= (ts->date (:time timeslot)) date))

(defn valid-time?
  "Is this time string valid?"
  [ts]
  (and (string? ts)
       (string? (re-matches iso-8601-regex ts))))
