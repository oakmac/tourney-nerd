(ns user
  (:require
    [clojure.walk :as walk]
    [jsonista.core :as json]
    [tourney-nerd.divisions :as tn.divisions]
    [tourney-nerd.events :as tn.events]
    [tourney-nerd.fields :as tn.fields]
    [tourney-nerd.groups :as tn.groups]))

; (def evt1
;   (-> (slurp "event1.txt")
;       json/read-value
;       walk/keywordize-keys))
