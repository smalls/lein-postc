(ns smallblog.templates
    (:use [clojure.string :only (join)])
    (:require [net.cgrand.enlive-html :as html])
    (:import [org.pegdown PegDownProcessor Extensions]))


(defn markdownify [post]
    (let [pgp (PegDownProcessor. Extensions/SMARTYPANTS)]
        (.markdownToHtml pgp post)))

(defn -main-div-post [blogname entries]
    (html/clone-for
        [entry entries]
        [:.posttitle] (html/content (:fmt-title entry))
        [:.postdate] (html/content (:date entry))
        [:.postbody] (html/html-content (:fmt-text entry))
        [:.permalink] (html/set-attr :href (:permalink entry))))


(html/deftemplate
    permalink "smallblog/templates/main.html"
    [blogname entry]
    [:h2#blogname] (html/content blogname)
    [:head :title] (html/content (str (:fmt-title entry) " - " blogname))
    [:div.post] (-main-div-post blogname [entry])
    [[:link (html/attr= :rel "stylesheet")]] #(assoc
                                                  % :attrs
                                                  (assoc
                                                      (:attrs %)
                                                      :href
                                                      (str "../"
                                                           (:href (:attrs %)))))
    [:div.pager] nil
    [:div.permadiv] nil)

(defn -is-first-page? [page pagination total-posts]
    (= 0 page))

(defn -number-of-pages [pagination total-posts]
    (int (/ total-posts pagination)))

(defn -is-last-page? [page pagination total-posts]
    (= page (-number-of-pages pagination total-posts)))

(defn -pager-text [page pagination total-posts]
    (str "page " (+ 1 page) " of " (+ 1 (-number-of-pages pagination total-posts))))

(html/deftemplate main "smallblog/templates/main.html"
                  [blogname entries]
                  [:h2#blogname] (html/content blogname)
                  [:head :title] (html/content blogname)
                  [:div.post] (-main-div-post blogname entries)
                  [:a#pager-newer] nil
                  [:a#pager-older] nil 
                  [:span#pager-text] nil)
