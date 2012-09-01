(defproject smallblog "1.0.0-SNAPSHOT"
            :description    "smallblog"
            :dependencies   [[org.clojure/clojure "1.2.1"]
                             [org.clojure/clojure-contrib "1.2.0"]
                             [clj-time "0.3.0"]
                             [enlive "1.0.0"]
                             [rhino/js "1.7R2"]
                             [com.thebuzzmedia/imgscalr-lib "3.2"]
                             [net.java.dev.jets3t/jets3t "0.8.1"]]
            :dev-dependencies   [[vimclojure/server "2.2.0"]
                                 [org.clojars.autre/lein-vimclojure "1.0.0"]]
            :repositories   {"The Buzz Media Maven Repository" ; imgscalr
                             "http://maven.thebuzzmedia.com"})
