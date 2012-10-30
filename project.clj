(defproject smallblog-static "1.0.0-SNAPSHOT"
            :description    "smallblog-static"
            :eval-in-leiningen true
            :license {:name "Eclipse Public License"
                      :url "http://www.eclipse.org/legal/epl-v10.html"}
            :url "http://www.splendous.net/"
            :min-lein-version "2.0.0"
            :dependencies   [[org.clojure/clojure "1.4.0"]
                             [enlive "1.0.0"]
                             [rhino/js "1.7R2"]
                             [org.imgscalr/imgscalr-lib "4.2"]
                             [net.java.dev.jets3t/jets3t "0.9.0"]
                             [org.reflections/reflections "0.9.8"]

                             ; not sure why this is required, but it made
                             ; reflections happy
                             [javax.servlet/servlet-api "2.5"]]
            
            :java-source-paths ["src/java"])
