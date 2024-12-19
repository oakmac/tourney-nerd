(ns com.oakmac.tourney-nerd.test-util
  "Utility functions for testing"
  (:require
   [clojure.edn :as edn]
   [clojure.java.io :as io]
   [clojure.walk :refer [keywordize-keys]]
   [jsonista.core :as jsonista]))

(defn load-test-resource-json-file
  [f]
  (-> f
    io/resource
    slurp
    jsonista/read-value
    keywordize-keys))

(defn load-test-resource-edn-file
  [f]
  (-> f
    io/resource
    slurp
    edn/read-string))
