# clj-load

A Clojure library using core.async for running concurrent processes repeatedly.

## Usage

Example of running an action repeatedly and concurrently

```
;; Implement the Strategy protocol for executing a single test
;; and determining if the results from that execution are and error or not.
(defrecord GetStrategy [url]
  Strategy
  (exec [_ _]
    (print ".")
    @(http/get url))
  (error? [_ r] (or (:error r) (>= (:status r) 400))))


(defn run []
  (println "Running...")
  ;; Run 10 at a time for 100 total executions
  ;; The lt Map can contain other properties as well and each (exec) call will
  ;; have access to that map.
  (let [lt {:count 100 :concurrent 10}
        ;; Create an instance of the Strategy
        strat (->GetStrategy "http://example.com")
        ;; Run everything and get the statistics back
        statistics (run-all lt strat)]
    (print-results statistics)))
```


## License

Copyright © 2014 Geoff Lane <geoff@zorchd.net>

Distributed under the The MIT License.
