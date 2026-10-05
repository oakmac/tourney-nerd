(ns com.oakmac.tourney-nerd.util.ids
  (:require
   [clojure.string :as str]
   [com.oakmac.tourney-nerd.util.base58 :refer [random-base58]]))

(def division-id-regex #"^division-[a-zA-Z0-9]{4,}$")
(def field-id-regex #"^field-[a-zA-Z0-9]{4,}$")
(def game-id-regex #"^game-[a-zA-Z0-9]{4,}$")
(def group-id-regex #"^group-[a-zA-Z0-9]{4,}$")
(def team-id-regex #"^team-[a-zA-Z0-9]{4,}$")
(def timeslot-id-regex #"^timeslot-[a-zA-Z0-9]{4,}$")

(defn- matches-id-regex? [regex id]
  (and (string? id)
       (some? (re-matches regex id))))

(defn division-id? [id] (matches-id-regex? division-id-regex id))
(defn field-id? [id] (matches-id-regex? field-id-regex id))
(defn game-id? [id] (matches-id-regex? game-id-regex id))
(defn group-id? [id] (matches-id-regex? group-id-regex id))
(defn team-id? [id] (matches-id-regex? team-id-regex id))
(defn timeslot-id? [id] (matches-id-regex? timeslot-id-regex id))

(def valid-id-types
  #{"division"
    "field"
    "game"
    "group"
    "team"
    "timeslot"})

(defn create-id-of-type [type]
  (str type "-" (random-base58)))

(defn create-division-id []
  (create-id-of-type "division"))

(defn create-field-id []
  (create-id-of-type "field"))

(defn create-game-id []
  (create-id-of-type "game"))

(defn create-group-id []
  (create-id-of-type "group"))

(defn create-team-id []
  (create-id-of-type "team"))

(defn create-timeslot-id []
  (create-id-of-type "timeslot"))

(defn create-id-of-same-type
  "Given an id, creates a new id of the same type."
  [id]
  (let [[head _tail] (str/split id #"-")]
    (assert (contains? valid-id-types head) (str "Unknown id type: " head))
    (create-id-of-type head)))
