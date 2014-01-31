(ns load.core-test
  (:require [clojure.test :refer :all]
            [load.core :refer :all]
            [load.strategies.test :refer :all]))

(deftest running-all
  (testing "can run all the things"
    (let [lt {:count 100 :concurrent 10}
          strat (->TestStrategy)
          results (run-all lt strat)]
      (is (not (nil? (:mean results))))
      (is (not (nil? (:median results))))
      (is (not (nil? (:max results))))
      (is (not (nil? (:min results))))
      (is (zero?  (:failure results)))
      (is (= 100 (:success results))))))
