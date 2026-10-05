(ns com.oakmac.tourney-nerd.test-util
  "Utility functions for testing"
  (:require
   [clojure.walk :refer [keywordize-keys]]
   #?@(:clj
       [[clojure.edn :as edn]
        [clojure.java.io :as io]
        [jsonista.core :as jsonista]]

       :cljs
       [["fs" :as fs]
        [cljs.reader :as reader]])))

(defn- slurp-test-resource
  "Returns the contents of a file in test-resources/ as a string."
  [f]
  #?(:clj (slurp (io/resource f))
     :cljs (fs/readFileSync (str "test-resources/" f) "utf8")))

(defn load-test-resource-json-file
  [f]
  (let [json-str (slurp-test-resource f)]
    (keywordize-keys
      #?(:clj (jsonista/read-value json-str)
         :cljs (js->clj (js/JSON.parse json-str))))))

(defn load-test-resource-edn-file
  [f]
  (let [edn-str (slurp-test-resource f)]
    #?(:clj (edn/read-string edn-str)
       :cljs (reader/read-string edn-str))))
