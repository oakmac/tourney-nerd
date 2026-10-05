(ns com.oakmac.tourney-nerd.fields-test
  (:require
   [clojure.test :refer [deftest is]]
   [com.oakmac.tourney-nerd.fields :as fields]))

(deftest create-field-test
  (let [f (fields/create-field 2 "B")]
    (is (true? (fields/valid-field? f)))
    (is (= "B" (:name f)))
    (is (= 2 (:order f))))
  (is (thrown? #?(:clj AssertionError :cljs js/Error)
               (fields/create-field 1 ""))
      "name must be at least 1 character"))

(deftest create-n-fields-test
  (let [fields-map (fields/create-n-fields 3)]
    (is (= 3 (count fields-map)))
    (is (every? fields/valid-field? (vals fields-map)))
    (is (= #{"Field 1" "Field 2" "Field 3"} (set (map :name (vals fields-map)))))))
