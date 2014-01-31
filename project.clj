(defproject load "0.1.0-SNAPSHOT"
  :description "Load testing with a bunch stuffs."
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/core.async "0.1.267.0-0d7780-alpha"]
                 [org.clojure/clojure-contrib "1.2.0"]
                 [reiddraper/simple-check "0.5.6" :scope "test"]]
  :main load.main)
