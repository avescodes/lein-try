(ns leiningen.try
  (:require [leiningen.repl :as lein-repl]
            [leiningen.core.classpath :as lein-cp]
            [clojure.edn :as edn]))

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

(defn resolve-deps
  "Resolve newly-added try-dependencies, adding them to classpath."
  [project]
  ;; TODO: I don't think this resolves the full hierarchy of dependencies
  (lein-cp/resolve-dependencies ::try-dependencies project :add-classpath? true)
  project)

(defn add-deps
  "Add list of dependencies to project and resolve them"
  [deps project]
  (assoc project ::try-dependencies deps))

(defn ^:no-project-needed try
  "Launch REPL with specified dependencies available.

  Usage:

    lein try [io.rkn/conformity \"0.2.1\"] [com.datomic/datomic-free \"0.8.4020.26\"]
    lein try io.rkn/conformity 0.2.1

  NOTE: lein-try does not require []"
  [project & args]
  (let [with-try-deps (partial add-deps (->dep-pairs args))]
    (-> project
      with-try-deps
      resolve-deps
      lein-repl/repl)))
