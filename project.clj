(defproject smallblog-static "1.0.0-SNAPSHOT"
            :description    "smallblog-static"
            :dependencies   [[org.clojure/clojure "1.4.0"]
                             [clj-time "0.4.3"]
                             [enlive "1.0.0"]
                             [rhino/js "1.7R2"]
                             [com.thebuzzmedia/imgscalr-lib "3.2"]
                             [net.java.dev.jets3t/jets3t "0.9.0"]]
            :repositories   {"The Buzz Media Maven Repository" ; imgscalr
                             "http://maven.thebuzzmedia.com"})
