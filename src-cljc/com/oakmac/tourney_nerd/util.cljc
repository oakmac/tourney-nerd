(ns com.oakmac.tourney-nerd.util)

(defn one? [x]
  (= x 1))

(defn half [x]
  (/ x 2))

(defn string-of-length?
  "Is s a String with a length between min-len and max-len (inclusive)?"
  [s min-len max-len]
  (and (string? s)
       (<= min-len (count s) max-len)))

(defn create-uuid []
  #?(:clj (.toString (java.util.UUID/randomUUID))
     :cljs (random-uuid)))

(defn str->int
  "convert s to an Integer"
  [s]
  #?(:clj  (java.lang.Integer/parseInt s)
     :cljs (js/parseInt s 10)))
