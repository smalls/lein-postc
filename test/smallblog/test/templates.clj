(ns smallblog.test.templates
    (:use		[smallblog.templates]
        [clojure.test]
        [clojure.string :only (join)]))

(deftest test-markdownify
         (let [reqcontent (str "some markdown content *italic* **bold**")
               expcontent (str "<p>some markdown content <em>italic</em> <strong>bold</strong></p>")]
             (is (= expcontent (markdownify reqcontent)))))

(deftest test-markdownify-attack
         (let [reqcontent (str "some content <script>evil</script>")
               expcontent (str "<p>some content evil</p>")]
             (is (= expcontent (markdownify reqcontent)))))
