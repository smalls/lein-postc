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
             (is (= "<p>just <em>something</em> else</p>" (:fmt-text (first entries))))))

(deftest test-write-permalink-posts
         "test writing permalink posts"
         (let [entries [(-format-entry "2012-02-03" "foo-bar-baz" "<p>something great</p>")]
               out-dir (clj-io/file "test/smallblog/test/output/test-write-permalink-posts")
               expected-post (clj-io/file "test/smallblog/test/output/test-write-permalink-posts/2012-02-03-foo-bar-baz.html")]
             (clj-io/delete-file expected-post :silently true)
             (clj-io/delete-file out-dir :silently true)
             (-write-permalink-posts out-dir entries)
             (is (.exists expected-post))
             (with-open [sw (StringWriter.)]
                 (clj-io/copy expected-post sw)
                 (is (<= 0 (.indexOf (.toString sw) (:fmt-text (first entries))))
                     (str "should have been found, not " (.toString sw))))))
