(ns smallblog.process
    (:use [smallblog.templates])
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

(defn -markdownify-file [file]
    "reads file"
    (markdownify (-read-file file)))

(defn -markdownify-dir
    "transform a sequence of markdown-formatted entries into formatted html"
    [dir]
    (let [files (.listFiles dir)]
        (if (not (.exists dir)) (throw (Exception. (str "missing:" dir))))
        (if (not (.isDirectory dir)) (throw (Exception. (str "not a directory:" dir))))
        (map -markdownify-file files)))
