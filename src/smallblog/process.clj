(ns smallblog.process
    (:use [smallblog.templates])
    (:require [clojure.java.io :as clj-io])
    (:import [java.io File FileInputStream StringWriter]))

(def blogname "XXXTBD")

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

(defn -format-entry [date title text]
    "format the entry - change titles-like-this to Titles Like This, markdownify text, etc"
    {:date date
     :permalink (str "/p/" date "-" title ".html")
     :raw-title title
     :fmt-title ""
     :raw-text text
     :fmt-text (markdownify text)})

(defn -markdownify-file [file]
    "reads file"
    (-format-entry (-date-from-filename (.getName file))
                   (-title-from-filename (.getName file))
                   (-read-file file)))

(defn -markdownify-dir [dir]
    "transform a sequence of markdown-formatted entries into formatted html"
    (let [files (.listFiles dir)]
        (if (not (.exists dir)) (throw (Exception. (str "missing:" dir))))
        (if (not (.isDirectory dir)) (throw (Exception. (str "not a directory:" dir))))
        (map -markdownify-file files)))

(defn -write-permalink-posts [out-dir entries]
    "write a list of markdownified entries out to the specified out-dir for permalinks"
    (.mkdir out-dir)
    (loop [entries entries]
        (let [entry (first entries)
              entry-out (permalink blogname entry)
              out-file (clj-io/file out-dir (str (:date entry) "-" (:raw-title entry) ".html"))]
            (do
                (with-open [out-writer (clj-io/writer out-file)]
                    (loop [out-writer out-writer
                           entry-out entry-out]
                        (clj-io/copy (first entry-out) out-writer)
                            (if (not (empty? (rest entry-out)))
                                (recur out-writer (rest entry-out)))))
                (if (not (empty? (rest entries)))
                    (recur (rest entries)))))))
