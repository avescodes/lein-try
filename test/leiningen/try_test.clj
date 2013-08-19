(ns leiningen.try-test
  (:require [clojure.test :refer :all]
            [leiningen.try :refer :all]))

(deftest ->dep-pairs-test
  (testing "the explicit use of version numbers"
    (is (= '[[clj-time "0.5.1"]]
           (->dep-pairs ["[clj-time" "0.5.1]"])))
    (is (= '[[clj-time "0.5.1"] [http-kit "2.1.5"]]
           (->dep-pairs ["[clj-time" "0.5.1]" "[http-kit" "2.1.5]"]))))
  (testing "the implicit use of 'RELEASE' if no version number is given"
    (is (= '[[clj-time "RELEASE"] [http-kit "2.1.5"]]
           (->dep-pairs ["clj-time" "[http-kit" "2.1.5]"])))
    (is (= '[[clj-time "RELEASE"] [http-kit "2.1.5"]]
           (->dep-pairs ["[clj-time]" "[http-kit" "2.1.5]"])))
    (is (= '[[clj-time "RELEASE"] [http-kit "2.1.5"]]
           (->dep-pairs ["[clj-time]" "http-kit" "2.1.5"])))
    (is (= '[[clj-time "RELEASE"] [http-kit "2.1.5"]]
           (->dep-pairs ["clj-time" "http-kit" "2.1.5"])))
    (is (= '[[clj-time "RELEASE"] [http-kit "RELEASE"]]
           (->dep-pairs ["clj-time" "http-kit"])))
    (is (= '[[clj-time "0.5.1"] [http-kit "RELEASE"]]
           (->dep-pairs ["clj-time" "0.5.1" "http-kit"]))))
  (testing "the explicit use of 'RELEASE'"
    (is (= '[[clj-time "RELEASE"] [http-kit "2.1.5"]]
           (->dep-pairs ["[clj-time" "RELEASE]" "[http-kit" "2.1.5]"])))
    (is (= '[[clj-time "RELEASE"] [http-kit "RELEASE"]]
           (->dep-pairs ["[clj-time" "RELEASE]" "[http-kit" "RELEASE]"])))
    (is (= '[[clj-time "RELEASE"] [http-kit "RELEASE"]]
           (->dep-pairs ["clj-time" "http-kit" "RELEASE"])))))

(deftest add-deps-test
  (let [add-try-deps #'leiningen.try/add-try-deps]
    (is (= (add-try-deps [[:a :b]] {})
           {:profiles {:try {:dependencies [[:a :b]]}}}))
    (is (= (add-try-deps [[:a :b]] {:dependencies [[:c :d]]})
           {:dependencies [[:c :d]]
            :profiles {:try {:dependencies [[:a :b]]}}}))
    (is (= (add-try-deps [[:c :d]] {:profiles {:try {:dependencies [[:a :b]]}}})
           {:profiles {:try {:dependencies [[:a :b] [:c :d]]}}}))
    (is (= (add-try-deps [[:e :f]] {:dependencies [[:c :d]]
                                    :profiles {:try {:dependencies [[:a :b]]}}})
           {:dependencies [[:c :d]]
            :profiles {:try {:dependencies [[:a :b] [:e :f]]}}}))))
