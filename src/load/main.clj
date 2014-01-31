(ns ^{:doc "Example application for running tests using load.core" }
  load.main
  (:require [load.core :refer :all]
            [load.strategies.loader :refer (strategy-for)]
            [clojure.string :as str]
            [clojure.tools.cli :refer (parse-opts)])
  (:gen-class))

(def cli-options
  "Command line option configuration"
  [["-c" "--concurrent NUMBER" "Number of concurrent processes to run"
    :default 2
    :parse-fn #(Integer/parseInt %)
    :validate [#(< 0 % 0x10000) "Must be a number between 0 and 65536"]]
   ["-n" "--number NUMBER" "Total number of times to run process"
    :default 10
    :parse-fn #(Integer/parseInt %)
    :validate [#(< 0 % 0x10000) "Must be a number between 0 and 65536"]]
   ["-t" "--type TEST" "The test to run"
    :default :test
    :parse-fn keyword
    :validate [#{:test :get} "Must be test or get"]]
   ["-h" "--help"]])

(defn usage [options-summary]
  "Print the usage summary"
  (->> ["clj-load"
        ""
        "Usage: load [options] arguments"
        ""
        "Options:"
        options-summary
        ""
        "Please refer to the docs for more information."]
       (str/join \newline)))

(defn error-msg [errors]
  "Print an error message"
  (str "The following errors occurred while parsing your command:\n\n"
       (str/join \newline errors)))

(defn exit [status msg]
  "Quit and print a message"
  (println msg)
  (System/exit status))

(defn build-load-test [{:keys [number concurrent]}] {:count number :concurrent concurrent})

(defn run [options arguments]
  "Run with the given options and arguments. Arguments are passed to the Strategy"
  (println "Running...")
  (let [lt (build-load-test options)
        strat (strategy-for (:type options) arguments)
        results (run-all lt strat)]
    (print-results results)))

(defn -main
  [& args]
  (let [{:keys [options arguments errors summary]} (parse-opts args cli-options)]
    (cond
      (:help options) (exit 0 (usage summary))
      errors (exit 1 (error-msg errors)))

    (run options arguments)))

