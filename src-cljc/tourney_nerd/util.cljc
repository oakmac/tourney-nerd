(ns tourney-nerd.util)

(defn one? [x]
  (= x 1))

(defn half [x]
  (/ x 2))

(defn create-uuid []
  #?(:clj (.toString (java.util.UUID/randomUUID))
     :cljs (random-uuid)))
