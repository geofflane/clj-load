(ns load.core-test
  (:require [clojure.test :refer :all]
            [load.core :refer :all]))

(defrecord TestStrategy []
  Strategy
  (testfn [_ lt]
    (do
      ;; (Thread/sleep 10)
      ;; (print ".")
      {:body "body" :status 200}))
  (error? [_ r] (>= (:status r) 400)))


(deftest running-all
  (testing "can run all the things"
    (let [lt {:count 100 :concurrent 10}
          strat (->TestStrategy)
          results (run-all lt strat)]
      (is (not (nil? (:mean results))))
      (is (not (nil? (:median results))))
      (is (not (nil? (:max results))))
      (is (not (nil? (:min results))))
      (is (zero?  (:failure results)))
      (is (= 100 (:success results))))))
