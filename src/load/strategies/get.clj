(ns load.strategies.get
  (:require [load.strategy :refer :all]
            [org.httpkit.client :as http]))

(defrecord GetStrategy [url]
  Strategy
  (exec [_ lt]
    (print ".")
    @(http/get url))
  (error? [_ r] (or (:error r) (>= (:status r) 400))))


