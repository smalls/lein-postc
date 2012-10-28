(ns leiningen.genblog
    (:use [smallblog.process :only (process process-static)])
    (:require [clojure.java.io :as clj-io]))

(defn only-dirs [fs]
    (filter #(and (.exists %) (.isDirectory %)) fs))

(defn genblog
    "Generate static html files from markdown-formatted posts"
    [project & keys]
    (let [src-dirs (map #(clj-io/file % "posts") (:source-paths project))
          static-dirs (map #(clj-io/file % "static") (:source-paths project))
          target-path (clj-io/file (:target-path project))
          intermediate-out-dir (clj-io/file target-path "web")
          out-dir (clj-io/file intermediate-out-dir "static")]
        (if (not (.exists target-path))
            (.mkdir target-path))
        (if (not (.exists intermediate-out-dir))
            (.mkdir intermediate-out-dir))
        (doseq [src-dir src-dirs]
            (if (or (not (.exists src-dir))
                    (not (.isDirectory src-dir)))
                (throw (Exception. (str "source must exist and be a directory: " src-dir)))))
        (process (only-dirs src-dirs) out-dir (:name project))
        (process-static (only-dirs static-dirs) out-dir)))
