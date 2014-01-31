(ns ^{:doc "Main algorithms for running things concurrently"}
  load.core
  (:refer-clojure :exclude [min max])
  (:require [load.stats :refer (avg median std-dev timed min max)]
            [load.strategy :refer :all]
            [clojure.core.async :refer (chan >!! <!! >! <! close! go go-loop timeout alt!)]))

(defn execute [lt strat]
  "Execute a single run of the load test"
  (timed [result duration]
         (exec strat lt)
         (assoc result :duration duration)))

(defn- process-results
  [total strat results]
  (let [durations (vec (map :duration results))
        {successes false failures true} (group-by #(error? strat %) results)]
    {:success (count successes)
     :failure (count failures)
     :min (min durations)
     :max (max durations)
     :mean (avg durations)
     :median (median durations)
     :std-dev (std-dev durations)
     :elapsed total}))

(def results (atom []))
(defn run-all [load-test strat]
  "Run the load test repeatedly and concurrently
   Results come out in a map like:
   {:success 10
     :failure 1
     :min 100
     :max 1000
     :mean 500
     :median 500
     :std-dev 15
     :elapsed 10000}"
  (let [result-chan (chan)
        run-chan (chan (:concurrent load-test))]
    (timed [_ duration]
           (do
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
                   (recur (dec count))))))
           (process-results duration strat @results))))

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
