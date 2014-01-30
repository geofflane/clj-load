(ns load.core
  (:require [load.stats :refer (avg median std-dev)]
            [clojure.core.async :refer (chan >!! <!! >! <! close! go go-loop timeout alt!)]))

(defprotocol Strategy
  "Protocol needed for running load tests and calculating the results"
  (testfn [this lt] "Run the load test")
  (error? [this result] "Is this result an error?"))

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
  "Run the load test repeatedly
   Results come out in a map like:
   {:success 10
     :failure 1
     :min 100
     :max 1000
     :mean 500
     :median 500
     :std-dev 15
     :elapsed 10000}
  "
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
  "Pretty print the results"
  (println "")
  (println "Success: " (:success results))
  (println "Failure " (:failure results))
  (println "Min: " (:min results))
  (println "Max: " (:max results))
  (println "Mean: " (:mean results))
  (println "Median: " (:median results))
  (println "Std Dev: " (:std-dev results))
  (println "Elapsed Time: " (:elapsed results)))
