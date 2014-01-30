(ns load.core-test
  (:require [clojure.test :refer :all]
            [load.core :refer :all]))

(deftest compute-average
  (testing "can compute average"
    (is (= 0 (avg [])))
    (is (= 2.0 (avg [1 2 3])))
    (is (= 2.0 (avg [2 2 2])))))


(deftest compute-median
  (testing "can compute median"
    (is (= 0 (median [])))
    (is (= 1 (median [1])))
    (is (= 2 (median [1 2 3])))
    (is (= 2.0 (median [2 2])))))


(defrecord TestStrategy []
  Strategy
  (testfn [_ lt]
    (do
      (Thread/sleep 1000)
      (print ".")
      {:body "body" :status 200}))
  (error? [_ r] (>= (:status r) 400)))


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
