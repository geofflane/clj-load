(ns ^{:doc "Protocol for running concurrent tests repeatedly"}
  load.strategy)

(defprotocol Strategy
  "Protocol needed for running load tests and calculating the results"
  (exec [this lt]
        "Run the load test. 'lt' is a Map containing the load test parameters.")
  (error? [this result]
          "Is this result an error? 'result' is the value returned from exec.
           This function should return true if the given result was an error."))




