(defproject lein-try "0.3.2-SNAPSHOT"
  :description "Try out libraries without adding them as dependencies"
  :url "https://github.com/rkneufeld/lein-try"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]]
  :profiles {:dev {:dependencies [[leiningen "2.2.0"]]}}
  :eval-in-leiningen true)
