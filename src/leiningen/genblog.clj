(ns leiningen.genblog
    (:use [smallblog.process :only (process)])
    (:require [clojure.java.io :as clj-io]))

(defn genblog
    "Generate static html files from markdown-formatted posts"
    [project & keys]
    (let [src-dirs (map #(clj-io/file % "posts") (:source-paths project))
          target-path (clj-io/file (:target-path project))
          out-dir (clj-io/file target-path "web")]
        (println "project" project)
        (if (not (.exists target-path))
            (.mkdir target-path))
        (doseq [src-dir src-dirs]
            (if (or (not (.exists src-dir))
                    (not (.isDirectory src-dir)))
                (throw (Exception. (str "source must exist and be a directory: " src-dir)))))
        (process src-dirs out-dir (:name project))))
