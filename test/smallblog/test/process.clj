(ns smallblog.test.process
    (:use [smallblog.process]
          [clojure.test]))

(deftest test-process-dir
         "test processing a test directory"
         (let [entries (process-dir (File. "test/smallblog/test/basic-sample"))]
             (is (= 1 (count entries)))
             (is (= "<p>just <em>something</em> else</p>" (first entries)))))
