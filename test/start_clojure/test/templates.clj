(ns start-clojure.test.templates
	(:use		[start-clojure.templates]
				[clojure.test]
				[clojure.string :only (join)]
				[clj-time.core :only (now date-time)])
	(:require	[clojure.contrib.string]))

(deftest test-markdownify
	(let [reqcontent (str "some markdown content *italic* **bold**")
			expcontent (str "<p>some markdown content <em>italic</em> <strong>bold</strong></p>")]
		(is (= expcontent (markdownify reqcontent)))))
