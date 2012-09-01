(ns smallblog.config
    (:import [java.lang System]))

(defn -get-from-env
    ([envname]
     (-get-from-env envname nil))
    ([envname default]
     (if (contains? (System/getenv) envname)
         (get (System/getenv) envname)
         default)))

; AWS configuration.  Read from AWS_ACCESS_KEY, AWS_SECRET_KEY, and
; IMAGE_BUCKET
(def aws-access-key (-get-from-env "AWS_ACCESS_KEY"))
(def aws-secret-key (-get-from-env "AWS_SECRET_KEY"))
(def image-bucket (-get-from-env "IMAGE_BUCKET"))
