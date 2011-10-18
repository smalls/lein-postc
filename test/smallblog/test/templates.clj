(ns smallblog.test.templates
	(:use		[smallblog.templates]
				[clojure.test]
				[clojure.string :only (join)]
				[clj-time.core :only (now date-time)])
	(:require	[clojure.contrib.string]))

(deftest test-markdownify
	(let [reqcontent (str "some markdown content *italic* **bold**")
			expcontent (str "<p>some markdown content <em>italic</em> <strong>bold</strong></p>")]
		(is (= expcontent (markdownify reqcontent)))))

(deftest test-markdownify-attack
	(let [reqcontent (str "some content <script>evil</script>")
			expcontent (str "<p>some content evil</p>")]
		(is (= expcontent (markdownify reqcontent)))))

(deftest test-domains-for-blog
	(let [domains [{:id 1 :blogid 1} {:id 2 :blogid 3} {:id 3 :blogid 1}]
			filtered-1 (-domains-for-blog 1 domains)
			filtered-2 (-domains-for-blog 2 domains)
			filtered-3 (-domains-for-blog 3 domains)]
		(is (= 2 (count filtered-1)))
		(is (= 0 (count filtered-2)))
		(is (= 1 (count filtered-3)))))
