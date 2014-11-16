(ns leiningen.try
  (:require [leiningen.core.project :as prj]
            [leiningen.core.main :as main]))

(defn- version-string?
  "Check if a given String represents a version number."
  [^String s]
  (or (contains? #{"RELEASE" "LATEST"} s)
      (Character/isDigit (first s))))

(def ->dep-pairs
  "From a sequence of command-line args describing dependency-version pairs,
  return a list of vector pairs. If no version is given, 'RELEASE' will be
  used.

  Example:
  (->dep-pairs [\"clj-time\" \"\\\"0.5.1\\\"]\"])
  ; -> ([clj-time \"0.5.1\"])

  (->dep-pairs [\"clj-time\" \"\\\"0.5.1\\\"\"])
  ; -> ([clj-time \"0.5.1\"])

  (->dep-pairs [\"clj-time\" \"conformity\"])
  ; -> ([clj-time \"RELEASE\"] [conformity \"RELEASE\"])"
  (letfn [(lazy-convert [args]
            (lazy-seq
              (when (seq args)
                (let [[^String artifact-str & rst] args
                      artifact (symbol artifact-str)]
                  (if-let [[^String v & nxt] (seq rst)]
                    (if (version-string? v)
                      (cons [artifact v] (lazy-convert nxt))
                      (cons [artifact "RELEASE"] (lazy-convert rst)))
                    (vector [artifact "RELEASE"]))))))]
    (fn [args]
      (lazy-convert args))))

(defn- disable-pedantic
  "Reduce `:pedantic?` level to `:warn` if `:abort` is given, maintain type to
   not cause unnecessary warnings."
  [{:keys [pedantic?] :as project}]
  (if (and pedantic? (= (name pedantic?) "abort"))
    (->> (condp #(% %2) pedantic?
           string? "warn"
           symbol? 'warn
           :warn)
         (assoc-in project [:profiles :try :pedantic?]))
    project))

(defn- add-try-deps
  "Add list of try-dependencies to project."
  [deps project]
  (update-in project [:profiles :try :dependencies] (comp vec concat) deps))

(defn add-reload-data-readers-injection
  "Add injection to project map that lets the REPL reload data readers. This is necessary
   in cases where no new JVM is spun up, thus `clojure.core` and the available data readers
   are not reloaded."
  [project]
  (if (or (= (:eval-in project) :leiningen)
          (:eval-in-leiningen project))
    (update-in project [:profiles :try :injections] conj
               `(or
                  (when-let [v# (resolve 'clojure.core/load-data-readers)]
                    (when-let [f# (var-get v#)]
                      (when (fn? f#)
                        (set! *data-readers* (merge *data-readers* (f#))))))
                  (println "Could not reload Data Readers ...")))
    project))

(defn- start-try-repl!
  "Resolve try-dependencies and start REPL."
  [project]
  (let [project (-> project
                    disable-pedantic
                    add-reload-data-readers-injection
                    ;; Necessary to update project map metadata Lein uses internally
                    prj/project-with-profiles
                    (prj/merge-profiles [:try]))]
    (main/apply-task "repl" project nil)))

(defn ^:no-project-needed ^:higher-order try
  "Launch REPL with specified dependencies available.

  Usage:

    lein try io.rkn/conformity \"0.2.1\" com.datomic/datomic-free \"0.8.4020.26\"
    lein try io.rkn/conformity 0.2.1
    lein try io.rkn/conformity # This uses the most recent version"
  [project & args]
  (let [dependencies (->dep-pairs args)
        project (add-try-deps dependencies project)]
    (start-try-repl! project)))
