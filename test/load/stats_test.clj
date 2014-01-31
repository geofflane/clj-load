(ns load.stats-test
  (:refer-clojure :exclude [min max])
  (:require [clojure.test :refer :all]
            [load.stats :refer :all]
            [simple-check.core :as sc]
            [simple-check.clojure-test :refer (defspec)]
            [simple-check.generators :as gen]
            [simple-check.properties :as prop]))

(def quickcheck-runs 100)

(deftest compute-min
  (testing "can compute minimum"
    (is (= 0 (min [])))
    (is (= 1 (min [1 2 3])))
    (is (= 1 (min [2 2 1])))))

(defspec min-integer-invariants
  quickcheck-runs
  (prop/for-all [v (gen/vector gen/int)]
                (= (min v) (or (first (sort v)) 0))))

(defspec min-floating-invariants
  quickcheck-runs
  (prop/for-all [v (gen/vector gen/ratio)]
                (= (min v) (or (first (sort v)) 0))))

(deftest compute-max
  (testing "can compute maximum"
    (is (= 0 (max [])))
    (is (= 3 (max [1 2 3])))
    (is (= 2 (max [2 2 1])))))


(defspec max-integer-invariants
  quickcheck-runs
  (prop/for-all [v (gen/vector gen/int)]
                (= (max v) (or (last (sort v)) 0))))

(defspec max-floating-invariants
  quickcheck-runs
  (prop/for-all [v (gen/vector gen/ratio)]
                (= (max v) (or (last (sort v)) 0))))

(deftest compute-average
  (testing "can compute average"
    (is (= 0 (avg [])))
    (is (= 2.0 (avg [1 2 3])))
    (is (= 2.0 (avg [2 2 2])))))

(defspec average-integer-invariants
  quickcheck-runs
  (prop/for-all [v (gen/vector gen/int)]
                (let [result (avg v)]
                  (<= (min v) result)
                  (>= (max v) result))))

(defspec average-floating-invariants
  quickcheck-runs
  (prop/for-all [v (gen/vector gen/ratio)]
                (let [result (avg v)]
                  (<= (min v) result)
                  (>= (max v) result))))

(deftest compute-median
  (testing "can compute median"
    (is (= 0 (median [])))
    (is (= 1 (median [1])))
    (is (= 2 (median [1 2 3])))
    (is (= 2.0 (median [2 2])))))

(defspec median-integer-invariants
  quickcheck-runs
  (prop/for-all [v (gen/vector gen/int)]
                (let [result (median v)]
                  (<= (min v) result)
                  (>= (max v) result))))

(defspec median-floating-invariants
  quickcheck-runs
  (prop/for-all [v (gen/vector gen/ratio)]
                (let [result (median v)]
                  (<= (min v) result)
                  (>= (max v) result))))
