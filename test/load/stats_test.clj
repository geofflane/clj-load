(ns load.stats-test
  (:require [clojure.test :refer :all]
            [load.stats :refer :all]))

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


