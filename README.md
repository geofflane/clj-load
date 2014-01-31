# clj-load

A Clojure library using core.async for running concurrent processes repeatedly.

## Usage

Example of running an action repeatedly and concurrently

```clojure
;; Implement the Strategy protocol for executing a single test
;; and determining if the results from that execution are an error or not.
(defrecord GetStrategy [url]
  Strategy
  (exec [this lt]
    (print ".")
    @(http/get url))
  (error? [_ r] (or (:error r) (>= (:status r) 400))))


(defn run []
  (println "Running...")
  ;; Run 10 at a time for 100 total executions
  ;; The lt Map can contain other properties as well and each (exec) call will
  ;; have this Map passed to it as an argument.
  (let [lt {:count 100 :concurrent 10}
        ;; Create an instance of the Strategy
        strat (->GetStrategy "http://example.com")
        ;; Run everything and get the statistics back
        statistics (run-all lt strat)]

;; Statistics come back in a Map:
;;   {:success 10
;;     :failure 1
;;     :min 100
;;     :max 1000
;;     :mean 500
;;     :median 500
;;     :std-dev 15
;;     :elapsed 10000}

;; Handy function to print out the results
    (print-results statistics)))
```


## License

Copyright © 2014 Geoff Lane <geoff@zorchd.net>

Distributed under the The MIT License.
