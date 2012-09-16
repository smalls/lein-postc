(ns smallblog.test.process
    (:use [smallblog.process]
          [clojure.test])
    (:require [clojure.java.io :as clj-io])
    (:import [java.io StringWriter]))

(deftest test-get-date-from-filename
         (is (= "2012-01-01" (-date-from-filename "2012-01-01-123-123")))
         (is (= "123-123" (-title-from-filename "2012-01-01-123-123"))))

(deftest test-markdownify-dir
         "test processing a test directory"
         (let [entries (-markdownify-dir (clj-io/file "test/smallblog/test/test-markdownify-dir"))]
             (is (= 1 (count entries)))
             (is (= "2012-01-01" (:date (first entries))))
             (is (= "<p>just <em>something</em> else</p>" (:raw (first entries))))))

(deftest test-write-direct-posts
         "test writing direct posts"
         (let [entries [{:date "2012-02-03" :title "foo-bar-baz" :raw "<p>something great</p>"}]
               out-dir (clj-io/file "test/smallblog/test/output/test-write-direct-posts")
               expected-post (clj-io/file "test/smallblog/test/output/test-write-direct-posts/2012-02-03-foo-bar-baz.html")]
             (try
                 (-write-direct-posts out-dir entries)
                 (is (.exists expected-post))
                 (with-open [sw (StringWriter.)]
                     (clj-io/copy expected-post sw)
                     (is (>= 0 (.indexOf (.toString sw) (:raw (first entries))))
                         (str "should have been found, not " (.toString sw))))
                 (finally
                     (clj-io/delete-file expected-post)
                     (clj-io/delete-file out-dir)))))

                 
