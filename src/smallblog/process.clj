(ns smallblog.process
    (:use [smallblog.templates])
    (:require [clojure.java.io :as clj-io])
    (:import [java.io File FileInputStream StringWriter]))

(defn -read-file [file]
    (if (not (.exists file)) (throw (Exception. (str "missing:" file))))
    (if (not (.isFile file)) (throw (Exception. (str "not a file:" file))))
    (let [sw (StringWriter.)
          stream (FileInputStream. file)]
        (try
            ; XXX use a library for this
            (loop [sw sw
                   stream stream]
                (let [b (.read stream)]
                    (if (= -1 b)
                        (.toString sw)
                        (do
                            (.write sw b)
                            (recur sw stream)))))
            (finally (.close stream)))))

(def -filename-split-regex #"^(\d+-\d+-\d+)-(.+)$")

(defn -date-from-filename [filename]
    (nth (re-find -filename-split-regex filename) 1))
(defn -title-from-filename [filename]
    (nth (re-find -filename-split-regex filename) 2))

(defn -markdownify-file [file]
    "reads file"
    {:date (-date-from-filename (.getName file))
     :title (-title-from-filename (.getName file))
     :raw (markdownify (-read-file file))})

(defn -markdownify-dir [dir]
    "transform a sequence of markdown-formatted entries into formatted html"
    (let [files (.listFiles dir)]
        (if (not (.exists dir)) (throw (Exception. (str "missing:" dir))))
        (if (not (.isDirectory dir)) (throw (Exception. (str "not a directory:" dir))))
        (map -markdownify-file files)))

(defn -write-direct-posts [out-dir entries]
    "write a list of markdownified entries out to the specified out-dir"
    (if (.exists out-dir) (throw (Exception. (str "output directory must not be present" out-dir))))
    (.mkdir out-dir)
    (loop [entries entries]
        (let [entry (first entries)
              out-file (clj-io/file out-dir (str (:date entry) "-" (:title entry) ".html"))]
            (do
                (clj-io/copy (:raw entry) out-file)
                (if (not (empty? (rest entries)))
                    (recur (rest entries)))))))
