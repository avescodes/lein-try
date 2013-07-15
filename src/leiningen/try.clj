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
       (map #(clojure.string/replace % #"\[|\]" ""))
       (partition 2)
       (map vec)
       (map #(update-in % [0] edn/read-string))))

(defn resolve-try-deps!
  "Resolve newly-added try-dependencies, adding them to classpath."
  [project]
  ;; TODO: I don't think this resolves the full hierarchy of dependencies
  (lein-cp/resolve-dependencies :dependencies project :add-classpath? true))

(defn add-try-deps
  "Add list of try-dependencies to project."
  [deps project]
  (update-in project [:dependencies] (comp vec concat) deps))

(defn start-repl!
  "Run REPL inside the same process to avoid losing classpath information."
  [project]
  (try
    ;; use #' to access private Vars so we can compile against
    ;; Leiningen 2.1.3 even tho' this code will throw an exception:
    (let [cfg {:host (#'lein-repl/repl-host project) 
               :port (#'lein-repl/repl-port project)}]
      (->> (lein-repl/server project cfg false) (lein-repl/client project)))
    (catch Exception _
      ;; Assume exception is due to bad signatures on server/client
      ;; and fall back to making it work in Leiningen 2.1.3:
      (lein-repl/repl (assoc project :eval-in :leiningen)))))

(defn ^:no-project-needed try
  "Launch REPL with specified dependencies available.

  Usage:

    lein try [io.rkn/conformity \"0.2.1\"] [com.datomic/datomic-free \"0.8.4020.26\"]
    lein try io.rkn/conformity 0.2.1

  NOTE: lein-try does not require []"
  [project & args]
  (let [project (add-try-deps (->dep-pairs args) project)]
    (resolve-try-deps! project)
    (start-repl! project)))
