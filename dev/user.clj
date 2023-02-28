(ns user
  (:require
    [clojure.walk :as walk]
    [jsonista.core :as json]
    [tourney-nerd.events :as tn.events]))

; (def evt1
;   (-> (slurp "event1.txt")
;       json/read-value
;       walk/keywordize-keys))
