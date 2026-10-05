(ns com.oakmac.tourney-nerd.divisions-test
  (:require
   [clojure.test :refer [deftest is]]
   [com.oakmac.tourney-nerd.divisions :as divisions]))

(deftest create-division-test
  (let [d (divisions/create-division 1 "Open")]
    (is (true? (divisions/valid-division? d)))
    (is (= "Open" (:name d)))
    (is (= 1 (:order d))))
  (is (thrown? #?(:clj AssertionError :cljs js/Error)
               (divisions/create-division 0 "Open"))
      "order must be a positive integer")
  (is (thrown? #?(:clj AssertionError :cljs js/Error)
               (divisions/create-division 1 "Op"))
      "name must be at least 3 characters"))

(deftest valid-division-test
  (is (false? (divisions/valid-division? nil)))
  (is (false? (divisions/valid-division? {:id "division-KmbM3AMx8xe3", :name "Open"})))
  (is (false? (divisions/valid-division? {:id "team-KmbM3AMx8xe3", :name "Open", :order 1}))))
