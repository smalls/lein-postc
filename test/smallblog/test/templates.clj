(ns smallblog.test.templates
    (:use		[smallblog.templates]
        [clojure.test]
        [clojure.string :only (join)]))

(deftest test-markdownify
         (let [reqcontent "some markdown content *italic* **bold**"
               expcontent "<p>some markdown content <em>italic</em> <strong>bold</strong></p>"]
             (is (= expcontent (markdownify reqcontent)))))

(deftest test-markdownify-attack
         "we're no longer sanitizing input, since this is no longer dynamic. Beware!"
         (let [reqcontent "some content <script>evil</script>"
               expcontent "<p>some content <script>evil</script></p>"]
             (is (= expcontent (markdownify reqcontent)))))
