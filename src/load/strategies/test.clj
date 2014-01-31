(ns load.strategies.test
  (:require [load.strategy :refer :all]))

(defrecord TestStrategy []
  Strategy
  (exec [_ lt]
    (Thread/sleep 100)
    (print ".")
    {:body "body" :status 200})
  (error? [_ r] (>= (:status r) 400)))


