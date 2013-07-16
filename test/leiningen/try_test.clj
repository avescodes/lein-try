(ns leiningen.try-test
  (:require [clojure.test :refer :all]
            [leiningen.try :refer :all]))

(deftest ->dep-pairs-test
  (is (= '[[clj-time "0.5.1"]]
         (->dep-pairs ["[clj-time" "0.5.1]"])))
  (is (= '[[clj-time "0.5.1"] [http-kit "2.1.5"]]
         (->dep-pairs ["[clj-time" "0.5.1]" "[http-kit" "2.1.5]"]))))

(deftest add-deps-test
  (let [add-try-deps #'leiningen.try/add-try-deps]
    (is (= {:dependencies [[:a :b]]}
           (add-try-deps [[:a :b]] {})))
    (is (= {:dependencies [[:a :b] [:c :d]]}
           (add-try-deps [[:c :d]] {:dependencies [[:a :b]]})))))
