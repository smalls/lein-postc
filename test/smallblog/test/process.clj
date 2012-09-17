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

(deftest test-write-posts
         "test writing posts"
         (let [entries [(-format-entry "2012-02-03" "foo-bar-baz" "<p>something great</p>")
                        (-format-entry "2012-02-04" "mediocre" "something pretty <i>mediocre</i>")]
               out-dir (.getAbsoluteFile (clj-io/file "test/smallblog/test/output/test-write-posts"))
               expected-index (clj-io/file "test/smallblog/test/output/test-write-posts/index.html")
               expected-post (clj-io/file "test/smallblog/test/output/test-write-posts/posts/2012-02-03-foo-bar-baz.html")
               expected-post-other (clj-io/file "test/smallblog/test/output/test-write-posts/posts/2012-02-04-mediocre.html")
               blogname "-_-NAME-_-"]
             (clj-io/delete-file expected-post :silently true)
             (clj-io/delete-file expected-post-other :silently true)
             (clj-io/delete-file expected-index :silently true)
             (clj-io/delete-file out-dir :silently true)
             (write-posts out-dir entries blogname)
             (is (.exists expected-post))
             (is (.exists expected-post-other))
             (is (.exists expected-index))

             ; test main index.html
             (with-open [sw (StringWriter.)]
                 (clj-io/copy expected-index sw)
                 (is (<= 0 (.indexOf (.toString sw) (:fmt-text (first entries))))
                     (str (:fmt-text (first entries)) "should have been found, but wasn't in:\n" (.toString sw)))
                 (is (<= 0 (.indexOf (.toString sw) (:fmt-text (last entries))))
                     (str (:fmt-text (last entries)) "should have been found, but wasn't in:\n" (.toString sw)))
                 (is (<= 0 (.indexOf (.toString sw) blogname))
                     (str blogname "should have been found, but wasn't in:\n" (.toString sw))))

             ; test permalink post
             (with-open [sw (StringWriter.)]
                 (clj-io/copy expected-post sw)
                 (is (<= 0 (.indexOf (.toString sw) (:fmt-text (first entries))))
                     (str (:fmt-text (first entries)) "should have been found, but wasn't in:\n" (.toString sw)))
                 (is (<= 0 (.indexOf (.toString sw) blogname))
                     (str blogname "should have been found, but wasn't in:\n" (.toString sw))))))
