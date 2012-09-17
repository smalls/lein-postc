(ns smallblog.process
    (:require [clojure.java.io :as clj-io]
              [smallblog.templates :as templates])
    (:import [java.io File FileInputStream StringWriter]))

(defn- ^String -read-file [file]
    (if (not (.exists file)) (throw (Exception. (str "missing:" file))))
    (if (not (.isFile file)) (throw (Exception. (str "not a file:" file))))
    (with-open [sw (StringWriter.)]
        (clj-io/copy file sw)
        (.toString sw)))

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
     :fmt-text (templates/markdownify text)})

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

(defn- write-output [out-file entry-text-seq]
    "write the output from entry-text-seq (which is a sequence)"
    (with-open [out-writer (clj-io/writer out-file)]
        (loop [out-writer out-writer
               entry-text-seq entry-text-seq]
            (clj-io/copy (first entry-text-seq) out-writer)
            (if (not (empty? (rest entry-text-seq)))
                (recur out-writer (rest entry-text-seq))))))

(defn- write-permalink-posts [out-dir entries blogname]
    "write a list of markdownified entries out to the specified out-dir for permalinks"
    (let [out-dir (clj-io/file out-dir "posts")]
        (.mkdir out-dir)
        (loop [entries entries]
            (let [entry (first entries)
                  entry-out (templates/permalink blogname entry)
                  out-file (.getAbsoluteFile (clj-io/file out-dir (str (:date entry) "-" (:raw-title entry) ".html")))]
                (do
                    (write-output out-file entry-out)
                    (if (not (empty? (rest entries)))
                        (recur (rest entries))))))))

(defn- write-index-posts [out-dir entries blogname]
    "write the list of posts out to the index file (possibly with additional paginated index files)"
    (.mkdir out-dir)
    (let [out-file (clj-io/file out-dir "index.html")]
        (write-output out-file (templates/main blogname entries))))

(defn write-posts [out-dir entries blogname]
    "write all posts out; see -write-index-posts and -write-permalink-posts"
    (write-index-posts out-dir entries blogname)
    (write-permalink-posts out-dir entries blogname))
