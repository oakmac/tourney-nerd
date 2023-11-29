(ns com.oakmac.tourney-nerd.util)

(defn one? [x]
  (= x 1))

(defn half [x]
  (/ x 2))

(defn create-uuid []
  #?(:clj (.toString (java.util.UUID/randomUUID))
     :cljs (random-uuid)))

(defn str->int
  "convert s to an Integer"
  [s]
  #?(:clj  (java.lang.Integer/parseInt s)
     :cljs (js/parseInt s 10)))
