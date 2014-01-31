(ns load.strategies.loader
  (:require [load.strategies.get :refer :all]
            [load.strategies.test :refer :all]))

(defn strategy-for
  [strat args]
  "Lookup a test strategy by key and create one"
  (case strat
    :test (->TestStrategy)
    :get (->GetStrategy (first args))))

