(ns load.core
  (:require [clojure.contrib.math :as math]
            [clojure.core.async :refer (chan >!! <!! >! <! close! go go-loop timeout alt!)]))

(defprotocol Strategy
  (testfn [this lt] "Run the load test")
  (error? [this result] "Is this result an error?"))

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

(defn execute [lt strat]
  "Execute the load test"
  (let [start (System/currentTimeMillis)
        result (testfn strat lt)
        dur (- (System/currentTimeMillis) start)]
    (assoc result :duration dur)))

(defn- process-results
  [start strat results]
  (let [durations (vec (map :duration results))
        {successes false failures true} (group-by #(error? strat %) results)]
    {:success (count successes)
     :failure (count failures)
     :min (apply min durations)
     :max (apply max durations)
     :mean (avg durations)
     :median (median durations)
     :std-dev (std-dev durations)
     :elapsed (- (System/currentTimeMillis) start)}))

(def results (atom []))
(defn run-all [load-test strat]
  "Run the load test repeatedly"
  (let [result-chan (chan)
        run-chan (chan (:concurrent load-test))
        start (System/currentTimeMillis)]
    (go-loop
      [count (:count load-test)]
      (when (pos? count)
        (go
          (>! run-chan 1)
          (>! result-chan (execute load-test strat))
          (<! run-chan)) ;; This pops off the run chan to allow another to push on
        (recur (dec count))))
    (<!!
      (go-loop
        [count (:count load-test)]
        (when (pos? count)
          (swap! results conj (<! result-chan))
          (flush) ;; make sure anything written in func is flushed out
          (recur (dec count)))))

    (process-results start strat @results)))

(defn print-results [results]
  (println "")
  (println "Success: " (:success results))
  (println "Failure " (:failure results))
  (println "Min: " (:min results))
  (println "Max: " (:max results))
  (println "Mean: " (:mean results))
  (println "Median: " (:median results))
  (println "Std Dev: " (:std-dev results))
  (println "Elapsed Time: " (:elapsed results)))
