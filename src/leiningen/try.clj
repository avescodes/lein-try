(ns leiningen.try
  (:require [leiningen.repl :as lein-repl]
            [leiningen.core.classpath :as lein-cp]
            [clojure.edn :as edn]))

(defn- version-string?
  "Check if a given String represents a version number."
  [^String s]
  (or (contains? #{"RELEASE" "LATEST"} s)
      (Character/isDigit (first s))))

(def ->dep-pairs
  "From a sequence of command-line args describing dependency-version pairs,
   return a list of vector pairs. Square braces in arg strings are ignored. If
   no version is given, 'RELEASE' will be used.

  Example:
  (->dep-pairs [\"clj-time\" \"\\\"0.5.1\\\"]\"])
  ; -> ([clj-time \"0.5.1\"])

  (->dep-pairs [\"[clj-time\" \"\\\"0.5.1\\\"]\"])
  ; -> ([clj-time \"0.5.1\"])
  
  (->dep-pairs [\"clj-time\" \"conformity\"])
  ; -> ([clj-time \"RELEASE\"] [conformity \"RELEASE\"])"
  (letfn [(lazy-convert [args]
            (lazy-seq 
              (when (seq args)
                (let [[^String artifact & rst] args
                      artifact (edn/read-string artifact)]
                  (if-let [[^String v & nxt] (seq rst)]
                    (if (version-string? v)
                      (cons [artifact v] (lazy-convert nxt))
                      (cons [artifact "RELEASE"] (lazy-convert rst)))
                    (vector [artifact "RELEASE"]))))))]
    (fn [args]
      (->> args
        (map #(clojure.string/replace % #"\[|\]" ""))
        lazy-convert))))

(defn- resolve-try-deps!
  "Resolve newly-added try-dependencies, adding them to classpath."
  [project]
  ;; TODO: I don't think this resolves the full hierarchy of dependencies
  (lein-cp/resolve-dependencies :dependencies project :add-classpath? true))

(defn- add-try-deps
  "Add list of try-dependencies to project."
  [deps project]
  (update-in project [:dependencies] (comp vec concat) deps))

(defn- start-repl!
  "Start a REPL inside our current process."
  [project]
  (try
    ;; Leiningen 2.1.3 and 2.2.0 have drastically different REPL APIs. Here's
    ;; the deal:
    ;;
    ;; * We have to use `#'` on `repl-host` and `repl-port` to compile.
    ;; * Leiningen 2.1.3 will raise an `ArityException` when we call `server`
    ;;   with one too many args. This is our clue we're in 2.1.3-land.
    (let [cfg {:host (#'lein-repl/repl-host project)
               :port (#'lein-repl/repl-port project)}
          uri (lein-repl/server project cfg false)]
      (lein-repl/client project uri))
    (catch clojure.lang.ArityException e
      ;; When we detect that we are in 2.1.3, fall back to a compatible
      ;; API-call
      (-> project
          (assoc :eval-in :leiningen)
          lein-repl/repl))))

(defn ^:no-project-needed try
  "Launch REPL with specified dependencies available.

  Usage:

    lein try [io.rkn/conformity \"0.2.1\"] [com.datomic/datomic-free \"0.8.4020.26\"]
    lein try io.rkn/conformity 0.2.1

  NOTE: lein-try does not require []"
  [project & args]
  (let [dependencies (->dep-pairs args)
        project (add-try-deps dependencies project)]
    (resolve-try-deps! project)
    (start-repl! project)))
