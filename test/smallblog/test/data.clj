(ns smallblog.test.data
    (:use [smallblog.config]
          [clojure.test])
    (:require [smallblog.data :as data]
              [clojure.java.io :as clj-io]))

(deftest test-get-content-type []
         (is (= ["image/jpeg" "jpeg"] (data/get-content-type "image/jpeg")))
         (is (= ["image/png" "png"] (data/get-content-type "image/png")))
         (is (= ["image/png" "png"] (data/get-content-type "image/gif")))
         (is (= ["image/png" "png"] (data/get-content-type "image/nonesuch"))))

(comment
(deftest test-image-name []
         (is (= "12-lamematt-full.jpg"
                (data/-image-name "lamematt.png")))
         (is (= "12-lamematt-blog.png"
                (data/-image-name "lamematt.png")))
         (is (= "abc-lamematt.bar-blog.jpg"
                (data/-image-name "lamematt.bar.png")))
         (is (= "abc-lamematt.bar-blog.png"
                (data/-image-name "lamematt.bar.png")))
         (is (= "abc-lamematt.bar-blog.jpg"
                (data/-image-name "lamematt.bar.")))
         (is (= "abc-lamematt-blog.jpg"
                (data/-image-name "lamematt"))))
                                           )

(deftest test-make-image
         "test make-image, get-image, and get-images"
         []
         (if-let [disable-test true]
             (println "this test contacts s3 and costs money; disabled")
             (do
                 (println "warning: this contacts s3 and is costing money")
                 (try
                     (let [path (clj-io/file "test/smallblog/test/data/IMG_0568.jpg")]
                         (is (.exists path))
                         (is (not "this part was at the wrong level; needs to call s3 directly")))))))
