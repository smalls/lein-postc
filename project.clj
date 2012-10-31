(defproject lein-postc "0.1.0"
            :description "lein-postc - generate blog-style pages from markdown posts"
            :license {:name "Eclipse Public License"
                      :url "http://www.eclipse.org/legal/epl-v10.html"}
            :url "http://b.splendous.net/"

            :eval-in-leiningen true
            :min-lein-version "2.0.0"

            :dependencies   [[org.clojure/clojure "1.4.0"]
                             [enlive "1.0.0"]
                             [rhino/js "1.7R2"]
                             [org.imgscalr/imgscalr-lib "4.2"]
                             [net.java.dev.jets3t/jets3t "0.9.0"]
                             [org.reflections/reflections "0.9.8"]
                             [javax.servlet/servlet-api "2.5"]]
            
            :java-source-paths ["src/java"])
