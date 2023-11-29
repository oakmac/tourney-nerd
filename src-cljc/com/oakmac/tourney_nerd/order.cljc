(ns com.oakmac.tourney-nerd.order)

;; TODO: allow the caller to pass in a custom sort-fn for maps that do not have an order keyword
(defn ensure-items-order
  "Given a collection of maps, ensure that every one has an :order key that increments sequentially."
  ([itms]
   (ensure-items-order itms :order))
  ([itms order-kwd]
   (let [return-map? (map? itms)
         itms-sequential (if return-map?
                           (let [keys2 (keys itms)
                                 vals2 (vals itms)]
                             (map-indexed
                               (fn [idx itm]
                                 (assoc itm ::original-key (nth keys2 idx)))
                               vals2))
                           itms)
         result (reduce
                  (fn [acc itm]
                    (if (integer? (get itm order-kwd))
                      (update-in acc [:itms-with-order-key] conj itm)
                      (update-in acc [:itms-without-order-key] conj itm)))
                  {:itms-with-order-key []
                   :itms-without-order-key []}
                  itms-sequential)
         sorted-ordered-itms (sort-by order-kwd (:itms-with-order-key result))
         sorted-unordered-itms (sort-by :name (:itms-without-order-key result))
         itms-with-new-order-vals (map-indexed
                                    (fn [idx itm]
                                      (assoc itm order-kwd (inc idx)))
                                    (concat sorted-ordered-itms sorted-unordered-itms))]
     (if return-map?
       (zipmap
         (map ::original-key itms-with-new-order-vals)
         (map #(dissoc % ::original-key) itms-with-new-order-vals))
       itms-with-new-order-vals))))
