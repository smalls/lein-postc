(ns smallblog.test.process
    (:use [smallblog.process]
          [clojure.test])
    (:import [java.io File]))

(deftest test-markdownify-dir
         "test processing a test directory"
         (let [entries (-markdownify-dir (File. "test/smallblog/test/test-markdownify-dir"))]
             (is (= 1 (count entries)))
             (is (= "<p>just <em>something</em> else</p>" (first entries)))))
