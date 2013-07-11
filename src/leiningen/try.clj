(ns leiningen.try
  (:require [leiningen.repl :as lein-repl]
            [clojure.edn :as edn]
            [alembic.still :as still]))

(def args ["[clj-time" "\"1.2.3\"]"])

(defn ->dep-pairs
  "From a sequence of command-line args describing dependency-version pairs,
  return a list of vector pairs. Square braces in arg strings are ignored.

  Example:
  (->dep-pairs [\"clj-time\" \"\\\"0.5.1\\\"]\"])
  ; -> ([clj-time \"0.5.1\"])

  (->dep-pairs [\"[clj-time\" \"\\\"0.5.1\\\"]\"])
  ; -> ([clj-time \"0.5.1\"])"
  [args]
  (->> args
       (mapv #(clojure.string/replace % #"\[|\]" ""))
       (#(update-in % [0] edn/read-string))
       (partition 2)
       (map vec)))

(defn ^:no-project-needed try
  "Launch REPL with specified dependencies available.

  Usage:

    lein try [io.rkn/conformity \"0.2.1\"] [com.datomic/datomic-free \"0.8.4020.26\"]
    lein try io.rkn/conformity 0.2.1

  NOTE: lein-try does not require [] "
  [project & args]
  (println "Fetching dependencies... (takes a while the first time)")
  (let [deps (->dep-pairs args)]
    (doseq [dep deps]
      (still/distill dep)
      (println "lein-try loaded" (pr-str dep))))
  (println)
  (lein-repl/repl project))
