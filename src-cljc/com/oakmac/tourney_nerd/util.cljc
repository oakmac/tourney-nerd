(ns com.oakmac.tourney-nerd.util)

(defn one? [x]
  (= x 1))

(defn half [x]
  (/ x 2))

(defn get-by-id
  "Returns the item with id from items, a map of items keyed by their id.
  items may be keyed by string or keyword, and id may be a string or keyword.
  Returns nil when there is no such item."
  [items id]
  (when id
    (let [id-str (if (keyword? id) (name id) (str id))]
      (or (get items (keyword id-str))
          (get items id-str)))))

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
