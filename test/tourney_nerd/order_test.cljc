(ns tourney-nerd.order-test
  (:require
    [clojure.test :refer [deftest is]]
    [tourney-nerd.order :refer [ensure-items-order]]))

(deftest ensure-order-test
  (is (= (ensure-items-order
           [{:name "aaa", :order 2}
            {:name "bbb", :order 1}
            {:name "ccc", :order 3}])
         [{:name "bbb", :order 1}
          {:name "aaa", :order 2}
          {:name "ccc", :order 3}]))
  (is (= (ensure-items-order
           [{:name "aaa", :order 2}
            {:name "bbb", :order 3}
            {:name "ccc", :order 4}])
         [{:name "aaa", :order 1}
          {:name "bbb", :order 2}
          {:name "ccc", :order 3}]))
  (is (= (ensure-items-order
           [{:name "aaa", :order 2}
            {:name "bbb", :order 3}
            {:name "ccc", :order 5}])
         [{:name "aaa", :order 1}
          {:name "bbb", :order 2}
          {:name "ccc", :order 3}]))
  (is (= (ensure-items-order
           [{:name "aaa", :order 2}
            {:name "bbb", :order 3}
            {:name "ccc"}])
         [{:name "aaa", :order 1}
          {:name "bbb", :order 2}
          {:name "ccc", :order 3}]))
  (is (= (ensure-items-order
           [{:name "aaa"}
            {:name "bbb", :order 3}
            {:name "ccc"}])
         [{:name "bbb", :order 1}
          {:name "aaa", :order 2}
          {:name "ccc", :order 3}]))
  (is (= (ensure-items-order
           [{:name "aaa", :display-order 2}
            {:name "bbb", :display-order 3}
            {:name "ccc"}]
           :display-order)
         [{:name "aaa", :display-order 1}
          {:name "bbb", :display-order 2}
          {:name "ccc", :display-order 3}]))
  (is (= (ensure-items-order
           {"1" {:name "aaa", :id "1", :order 1}
            "2" {:name "bbb", :id "2"}
            "3" {:name "ccc", :id "3", :order 3}})
         {"1" {:name "aaa", :id "1", :order 1}
          "2" {:name "bbb", :id "2", :order 3}
          "3" {:name "ccc", :id "3", :order 2}})))
