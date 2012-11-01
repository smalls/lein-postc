(defproject lein-postc "0.2.0-SNAPSHOT"
            :description "lein-postc - generate blog-style pages from markdown posts"
            :license {:name "Eclipse Public License"
                      :url "http://www.eclipse.org/legal/epl-v10.html"}
            :url "http://b.splendous.net/"

            :eval-in-leiningen true
            :min-lein-version "2.0.0"

            :dependencies   [[org.clojure/clojure "1.4.0"]
                             [enlive "1.0.0"]
                             [org.pegdown/pegdown "1.2.0"]
                             [org.reflections/reflections "0.9.8"]
                             [javax.servlet/servlet-api "2.5"]]
            
            :java-source-paths ["src/java"])
