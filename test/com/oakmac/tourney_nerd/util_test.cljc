(ns com.oakmac.tourney-nerd.util-test
  (:require
   [clojure.test :refer [deftest is testing]]
   [com.oakmac.tourney-nerd.util :as util]))

(def keyword-keyed {:team-aaaaaaaa {:id "team-aaaaaaaa", :name "A"}})
(def string-keyed {"team-aaaaaaaa" {:id "team-aaaaaaaa", :name "A"}})

(deftest get-by-id-test
  (testing "any combination of string / keyword map keys and id"
    (is (= "A" (:name (util/get-by-id keyword-keyed "team-aaaaaaaa"))))
    (is (= "A" (:name (util/get-by-id keyword-keyed :team-aaaaaaaa))))
    (is (= "A" (:name (util/get-by-id string-keyed "team-aaaaaaaa"))))
    (is (= "A" (:name (util/get-by-id string-keyed :team-aaaaaaaa)))))
  (testing "missing"
    (is (nil? (util/get-by-id keyword-keyed "team-bbbbbbbb")))
    (is (nil? (util/get-by-id keyword-keyed nil)))
    (is (nil? (util/get-by-id nil "team-aaaaaaaa")))))
