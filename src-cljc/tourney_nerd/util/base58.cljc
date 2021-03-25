(ns tourney-nerd.util.base58
  (:require
    [clojure.string :as str]))

(def base58-chars
  "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz")

(def default-length 12)

(defn random-base58
  "returns a random base58 string"
  ([]
   (random-base58 default-length))
  ([len]
   (apply str (take len (repeatedly #(rand-nth base58-chars))))))
