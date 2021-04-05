(ns tourney-nerd.groups
  (:require
    [clojure.set :as set]
    [malli.core :as malli]
    [tourney-nerd.util.base58 :refer [random-base58]]))

(def group-id-regex #"^group-[a-zA-Z0-9]{4,}$")

(defn random-group-id []
  (str "group-" (random-base58)))
