(ns smallblog.process
    (:require [clojure.java.io :as clj-io]
              [clojure.string :as clj-str]
              [smallblog.templates :as templates])
    (:import [java.io File FileInputStream StringWriter]
             [org.reflections Reflections]
             [org.reflections.scanners Scanner ResourcesScanner]))

(defn- ^String -read-file [file]
    (if (not (.exists file)) (throw (Exception. (str "missing:" file))))
    (if (not (.isFile file)) (throw (Exception. (str "not a file:" file))))
    (with-open [sw (StringWriter.)]
        (clj-io/copy file sw)
        (.toString sw)))

(def -filename-split-regex #"^(\d+-\d+-\d+)-(.+?)(?:\.md)?$")

(defn -date-from-filename [filename]
    (nth (re-find -filename-split-regex filename) 1))
(defn -title-from-filename [filename]
    (nth (re-find -filename-split-regex filename) 2))

(defn -format-entry
    "format the entry - change titles-like-this to Titles Like This,
    markdownify text, etc"
    [date title text]
    {:date date
     :permalink (str "p/" date "-" title ".html")
     :raw-title title
     :fmt-title ""
     :raw-text text
     :fmt-text (templates/markdownify text)})

(defn -markdownify-file
    "reads file"
    [file]
    (-format-entry (-date-from-filename (.getName file))
                   (-title-from-filename (.getName file))
                   (-read-file file)))

(defn -markdownify-dir
    "transform a sequence of markdown-formatted entries into entries"
    [dir]
    (let [files (.listFiles dir)]
        (if (not (.exists dir)) (throw (Exception. (str "missing:" dir))))
        (if (not (.isDirectory dir))
            (throw (Exception.  (str "not a directory:" dir))))
        (rseq (vec (sort-by :date (map -markdownify-file files))))))

(defn- write-output
    "write the output from entry-text-seq (which is a sequence)"
    [out-file entry-text-seq]
    (with-open [out-writer (clj-io/writer out-file)]
        (loop [out-writer out-writer
               entry-text-seq entry-text-seq]
            (clj-io/copy (first entry-text-seq) out-writer)
            (if (not (empty? (rest entry-text-seq)))
                (recur out-writer (rest entry-text-seq))))))

(defn- write-permalink-posts
    "write a list of markdownified entries out to the specified out-dir for
    permalinks"
    [out-dir entries blogname]
    (let [out-dir (clj-io/file out-dir "p")]
        (.mkdir out-dir)
        (loop [entries entries]
            (let [entry (first entries)
                  entry-out (templates/permalink blogname entry)
                  out-file (.getAbsoluteFile
                               (clj-io/file out-dir (str (:date entry) "-" (:raw-title entry) ".html")))]
                (do
                    (write-output out-file entry-out)
                    (if (not (empty? (rest entries)))
                        (recur (rest entries))))))))

(defn- write-index-posts
    "write the list of posts out to the index file (possibly with additional
    paginated index files)"
    [out-dir entries blogname]
    (.mkdir out-dir)
    (let [out-file (clj-io/file out-dir "index.html")]
        (write-output out-file (templates/main blogname entries))))

(defn -write-posts
    "write all posts out; see -write-index-posts and -write-permalink-posts"
    [entries out-dir blogname]
    (write-index-posts out-dir entries blogname)
    (write-permalink-posts out-dir entries blogname))

(defn process
    "read post source files, process them, and write them to the output
    directory. returns nil."
    [in-dirs out-dir blogname]
    (doseq [in-dir in-dirs]
        (-write-posts (-markdownify-dir in-dir) out-dir blogname)))

(defn- recurse-dir
    "Helper method for files-to-copy; handles recursion"
    ([in-dir out-dir] (recurse-dir (.listFiles in-dir) out-dir []))
    ([files out-dir current-list]
     (let [f (first files)]
         (if (.isDirectory f)
             (concat
                 (if (empty? (rest files))
                     current-list
                     (recurse-dir (rest files) out-dir current-list))
                 (recurse-dir (.listFiles f)
                               (clj-io/file out-dir (.getName f))
                               []))
             (let [new-list (conj current-list
                                  {:in-file f :out-file (clj-io/file out-dir (.getName f))})]
                 (if (empty? (rest files))
                     new-list
                     (recurse-dir (rest files) out-dir new-list)))))))

(defn- recurse-cp
    "recurse over classpath resources in the given path"
    [resource-root out-dir]
    (let [cp-resource-root (clj-str/replace resource-root #"/" ".")
          scanner (ResourcesScanner.) 
          reflections (Reflections. (into-array Object [cp-resource-root scanner]))
          resources (.getResources reflections #".*")]
        (loop [in-resources resources
               out-resources []]
            (if (empty? in-resources)
                out-resources
                (recur (rest in-resources)
                       (let [in-resource (first in-resources)]
                           (println "in-resource" in-resource)
                           (println "in-resource as resource"
                                    (clj-io/resource in-resource))
                       (conj out-resources {:in-file (clj-io/resource in-resource)
                                            :out-file (clj-io/file
                                                          out-dir in-resource)})))))))

(defn files-to-copy
    "Returns a list of maps with :in-file :out-file keys populated.
    :in-file may be a URL or a File. :out-file will be a File.
    Will throw an exception if there's a conflict between two source files
    with the same target."
    [static-dirs out-dir]
    (let [f-t-c (concat
                    (recurse-cp "static" out-dir)
                    (loop [in-dirs static-dirs
                           files []]
                        (if (empty? in-dirs)
                            files
                            (recur (rest in-dirs)
                                   (concat files (recurse-dir (first in-dirs) out-dir))))))]
        (loop [files f-t-c
               touched {}]
            (if (not (empty? files))
                (let [f (first files)]
                    (if (contains? touched (:out-file f))
                        (throw (Exception. (str "duplicate entries from "
                                                (:in-file f) " and "
                                                (get touched (:out-file f)))))
                        (recur (rest files) (assoc touched (:out-file f) (:in-file f)))))))
        f-t-c))

(defn process-static
    "copy static files into place"
    [static-dirs out-dir]
    (let [files (files-to-copy static-dirs out-dir)]
        (loop [files files]
            (if (not (empty? files))
                (let [f (first files)
                      parent (.getParentFile (:out-file f))]
                    (if (not (.exists parent))
                        (.mkdirs parent))
                    (with-open [input (clj-io/input-stream (:in-file f))]
                        (clj-io/copy input (:out-file f)))
                    (recur (rest files)))))))
