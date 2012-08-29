(ns smallblog.templates
    (:use [clojure.string :only (join)]
          [smallblog.config])
    (:require [net.cgrand.enlive-html :as html]
              [clj-time.core :as clj-time]
              [clj-time.format :as clj-time-format]
              [clj-time.coerce :as clj-time-coerce])
    (:import [org.mozilla.javascript Context ScriptableObject]))

(def *image-full* "full")
(def *image-blog* "blog")
(def *image-thumb* "thumb")


(def date-output-format (clj-time-format/formatter "dd MMM yyyy"))

(defn markdownify [post]
    (let [cx (Context/enter)
          scope (.initStandardObjects cx)
          input (Context/javaToJS post scope)
          script (str
                     (html/get-resource "smallblog/Markdown.Converter.js" slurp)
                     "window = {Markdown: {Converter: Markdown.Converter}};"
                     (html/get-resource "smallblog/Markdown.Sanitizer.js" slurp)
                     "san = window.Markdown.getSanitizingConverter;"
                     "san().makeHtml(input);")]
        (try
            (ScriptableObject/putProperty scope "input" input)
            (let [result (.evaluateString cx scope script "<cmd>" 1 nil)]
                (Context/toString result))
            (finally (Context/exit)))))

(defn -main-div-post [ctx]
    (html/clone-for [item (:posts ctx)]
                    [:.posttitle] (html/content (:title item))
                    [:.postdate] (html/content
                                     (clj-time-format/unparse
                                         date-output-format
                                         (clj-time-coerce/from-date (:created_date item))))
                    [:.postbody] (html/html-content (:converted_content item))
                    [:.permalink] (html/set-attr :href (:XXX-permalink-url ctx))
                    [:.permalink] (html/content (:XXX-permalink-url ctx))))

(defn -is-first-page? [page pagination total-posts]
    (= 0 page))

(defn -number-of-pages [pagination total-posts]
    (int (/ total-posts pagination)))

(defn -is-last-page? [page pagination total-posts]
    (= page (-number-of-pages pagination total-posts)))

(defn -pager-text [page pagination total-posts]
    (str "page " (+ 1 page) " of " (+ 1 (-number-of-pages pagination total-posts))))

(html/deftemplate main "smallblog/templates/main.html"
                  [ctx]
                  [:p#blogname] (html/content (:blogname ctx))
                  [:head :title] (html/content (:blogname ctx))
                  [:div.post] (-main-div-post ctx)
                  [:a#pager-newer] (if (:XXX-is-first-page ctx)
                                       nil)
                  [:a#pager-older] (if (:XXX-is-last-page ctx)
                                       nil)
                  [:span#pager-text] (html/content (-pager-text
                                                       (:page ctx) (:pagination ctx)
                                                       (:total-posts ctx))))
