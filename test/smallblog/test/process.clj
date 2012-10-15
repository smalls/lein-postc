(ns smallblog.test.process
    (:use [smallblog.process]
          [clojure.test])
    (:require [clojure.java.io :as clj-io])
    (:import [java.io StringWriter]))

(deftest test-get-from-filename
         (is (= "2012-01-01" (-date-from-filename "2012-01-01-123-123")))
         (is (= "123-123" (-title-from-filename "2012-01-01-123-123")))
         (is (= "123-123" (-title-from-filename "2012-01-01-123-123.md"))))

(deftest test-markdownify-dir
         "test processing a test directory"
         (let [entries (-markdownify-dir (clj-io/file "test/smallblog/test/test-markdownify-dir"))]
             (is (= 3 (count entries)))
             (is (= "2012-01-01" (:date (first entries))))
             (is (= "2011-12-12" (:date (second entries))))
             (is (= "<p>just <em>something</em> else</p>" (:fmt-text (first entries))))))

(deftest test-write-posts
         "test writing posts"
         (let [entries [(-format-entry "2012-02-03" "foo-bar-baz" "<p>something great</p>")
                        (-format-entry "2012-02-04" "mediocre" "something pretty <i>mediocre</i>")]
               out-dir (.getAbsoluteFile (clj-io/file "test/smallblog/test/output/test-write-posts"))
               expected-index (clj-io/file "test/smallblog/test/output/test-write-posts/index.html")
               expected-post (clj-io/file "test/smallblog/test/output/test-write-posts/p/2012-02-03-foo-bar-baz.html")
               expected-post-other (clj-io/file "test/smallblog/test/output/test-write-posts/p/2012-02-04-mediocre.html")
               blogname "-_-NAME-_-"]
             (clj-io/delete-file expected-post :silently true)
             (clj-io/delete-file expected-post-other :silently true)
             (clj-io/delete-file expected-index :silently true)
             (clj-io/delete-file out-dir :silently true)
             (-write-posts entries out-dir blogname)
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

(defn delete-file-recursively
    "Delete file f. If it's a directory, recursively delete all its contents.
    Raise an exception if any deletion fails unless silently is true.
    Shamelessly copied from
    https://github.com/richhickey/clojure-contrib/blob/a1c66df5287776b4397cf3929a5f498fbb34ea32/src/main/clojure/clojure/contrib/java_utils.clj#L185
    since it doesn't appear to exist in libraries anymore."
    [f & [silently]]
    (let [f (clj-io/file f)]
        (if (.isDirectory f)
            (doseq [child (.listFiles f)]
                (delete-file-recursively child silently)))
        (clj-io/delete-file f silently)))
               
(deftest test-files-to-copy
         (let [in-dir (.getAbsoluteFile (clj-io/file "test/smallblog/test/data/files-to-copy-1"))
               out-dir (clj-io/file "/tmp")
               cp-only-files (files-to-copy [] out-dir)
               files (files-to-copy [in-dir] out-dir)
               in-files (map #(:in-file %) files)
               out-files (map #(:out-file %) files)]
             (is (=
                     (+ 2 (count cp-only-files))
                     (count files)))
             (is (some #(= % (clj-io/file in-dir "hi.js")) in-files))
             (is (some #(= % (clj-io/file out-dir "hi.js")) out-files))
             (is (some #(= % (clj-io/file in-dir "nested" "bye.js")) in-files))
             (is (some #(= % (clj-io/file out-dir "nested" "bye.js")) out-files))))

(deftest test-process-static
         (let [in-dirs [(clj-io/file "test/smallblog/test/data/static-dir-1")
                        (clj-io/file "test/smallblog/test/data/static-dir-2")]
               out-dir (clj-io/file "test/smallblog/test/output/test-write-static")
               expected-file-1 (clj-io/file out-dir "bar.js")
               expected-js-dir (clj-io/file out-dir "js")
               expected-file-2 (clj-io/file expected-js-dir "baz.js")
               expected-file-3 (clj-io/file expected-js-dir "foo.js")]

             (delete-file-recursively out-dir :silently true)

             (process-static in-dirs out-dir)

             (is (.exists expected-file-1))
             (is (.exists expected-file-2))
             (is (.exists expected-file-3))))

(deftest test-process-static-conflict
         (let [in-dirs [(clj-io/file "test/smallblog/test/data/static-dir-1")
                        (clj-io/file "test/smallblog/test/data/static-dir-2")
                        (clj-io/file "test/smallblog/test/data/static-dir-conflict")]
               out-dir (.getAbsoluteFile (clj-io/file "test/smallblog/test/output/test-write-static-conflict"))]

             (delete-file-recursively out-dir :silently true)

             (is (thrown? Exception (process-static in-dirs out-dir)))))
