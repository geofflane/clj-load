(ns load.main
  (:require [load.core :refer :all])
  (:gen-class))

(defrecord TestStrategy []
  Strategy
  (testfn [_ lt]
    (do
      (Thread/sleep 1000)
      (print ".")
      {:body "body" :status 200}))
  (error? [_ r] (>= (:status r) 400)))

(defn -main
  []
  (println "Running...")
  (let [lt {:count 100 :concurrent 10}
        strat (->TestStrategy)
        results (run-all lt strat)]
    (print-results results)))

