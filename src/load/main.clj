(ns load.main
  (:require [load.core :refer :all])
  (:gen-class))

(defn funx [lt]
  (do
    (Thread/sleep 1000)
    (print ".")
    {:body "body" :status 200}))

(defn error? [r] (>= (:status r) 400))

(defn -main
  []
  (println "Running...")
  (let [lt {:count 100 :concurrent 10}
        results (run-all lt funx error?)]
    (print-results results)))

