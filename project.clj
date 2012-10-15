(defproject smallblog-static "1.0.0-SNAPSHOT"
            :description    "smallblog-static"
            :eval-in-leiningen true
            :license {:name "Eclipse Public License"
                      :url "http://www.eclipse.org/legal/epl-v10.html"}
            :dependencies   [[org.clojure/clojure "1.4.0"]
                             [enlive "1.0.0"]
                             [rhino/js "1.7R2"]
                             [com.thebuzzmedia/imgscalr-lib "3.2"]
                             [net.java.dev.jets3t/jets3t "0.9.0"]
                             [org.reflections/reflections "0.9.8"]]
            :repositories   {"The Buzz Media Maven Repository" ; imgscalr
                             "http://maven.thebuzzmedia.com"})
