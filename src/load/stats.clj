(ns load.stats
  (:refer-clojure :exclude [min max])
  (:require [clojure.contrib.math :as math]))

(defmacro timed
  [[result duration] body after]
  `(let [start# (System/currentTimeMillis)
         ~result ~body]
     (let [~duration (- (System/currentTimeMillis) start#)]
       ~after)))

(defn- zero-if-empty?
  [func args]
  (if (empty? args) 0
    (func args)))

(defn min
  [intervals]
  "Clojure core min does not handle empty lists"
  (zero-if-empty? (partial apply clojure.core/min) intervals))

(defn max
  [intervals]
  "Clojure core max does not handle empty lists"
  (zero-if-empty? (partial apply clojure.core/max) intervals))

(defn avg
  "Statistical mean calculation"
  [intervals]
  (if
    (empty? intervals) 0
    (/ (reduce + intervals) (float (count intervals)))))

(defn std-dev
  "Statistical standard deviation calculation."
  [intervals]
  (let [mean (avg intervals)
        diffs (map #(math/expt (- % mean) 2) intervals)
        sq-diff-sum (reduce + diffs)
        variance (/ sq-diff-sum (float (count intervals)))]
    (math/sqrt variance)))

(defn median
  "Statistical median value calculation"
  [intervals]
  (let [len (count intervals)]
    (cond
      (zero? len) 0
      (= 1 len) (first intervals)
      (odd? len) (intervals (quot len 2))
      :else (let [end (quot len 2)
                  start (dec end)]
              (avg (subvec intervals start (inc end)))))))


