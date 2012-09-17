(ns smallblog.templates
    (:use [clojure.string :only (join)]
          [smallblog.config])
    (:require [net.cgrand.enlive-html :as html])
    (:import [org.mozilla.javascript Context ScriptableObject]))

(def image-full "full")
(def image-blog "blog")
(def image-thumb "thumb")


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

(defn -main-div-post [blogname entries]
    (html/clone-for [entry entries]
                    [:.posttitle] (html/content (:fmt-title entry))
                    [:.postdate] (html/content (:date entry))
                    [:.postbody] (html/html-content (:fmt-text entry))
                    [:.permalink] (html/set-attr :href (:permalink entry))
                    [:.permalink] (html/content (:permalink entry))))


(html/deftemplate permalink "smallblog/templates/main.html"
                  [blogname entry]
                  [:p#blogname] (html/content blogname)
                  [:head :title] (html/content blogname)
                  [:div.post] (-main-div-post blogname [entry])
                  [:div.pager] nil)

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
