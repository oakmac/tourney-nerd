(ns com.oakmac.tourney-nerd.events-test
  (:require
    [clojure.test :refer [deftest is]]
    [com.oakmac.tourney-nerd.events :as events]
    [com.oakmac.tourney-nerd.test-util :refer [load-test-resource-edn-file]]))

(def unordered-event (load-test-resource-edn-file "unordered-event.edn"))
(def ordered-event (load-test-resource-edn-file "ordered-event.edn"))

(deftest ensure-order-test
  (is (= (events/ensure-order unordered-event)
         ordered-event)))
