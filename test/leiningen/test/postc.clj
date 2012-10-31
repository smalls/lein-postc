(ns leiningen.test.postc
    (:use [leiningen.postc]
          [clojure.test])
    (:require [clojure.java.io :as clj-io]))

(deftest test-only-dirs
         (let [fed (clj-io/file "test/smallblog/test")
               fed2 (clj-io/file "test/smallblog")
               fed3 (clj-io/file "test")
               fef (clj-io/file ".gitignore")
               fne (clj-io/file "foobarbaz")
               reduced (only-dirs (list fed fef fne))
               reduced3 (only-dirs (list fed fed2 fed3))]
             (is (.exists fed))
             (is (.isDirectory fed))
             (is (.exists fef))
             (is (not (.isDirectory fef)))
             (is (not (.exists fne)))
             (is (= 1 (count reduced)))
             (is (= (first reduced) fed))
             (is (= 3 (count reduced3)))))
