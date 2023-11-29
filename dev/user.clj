(ns user
  (:require
    [clojure.walk :as walk]
    [jsonista.core :as json]
    [com.oakmac.tourney-nerd.divisions :as tn.divisions]
    [com.oakmac.tourney-nerd.events :as tn.events]
    [com.oakmac.tourney-nerd.fields :as tn.fields]
    [com.oakmac.tourney-nerd.groups :as tn.groups]))

; (def evt1
;   (-> (slurp "event1.txt")
;       json/read-value
;       walk/keywordize-keys))
