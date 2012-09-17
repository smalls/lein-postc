(ns smallblog.data
    (:use [smallblog.templates :only (markdownify image-full image-blog image-thumb)]
          [smallblog.config]
          [clojure.string :only (split)])
    (:require [clojure.string :as str])
    (:import [java.util Calendar]
             [java.io File ByteArrayInputStream ByteArrayOutputStream FileInputStream]
             [javax.imageio ImageIO]
             [java.awt.image BufferedImageOp]
             [com.thebuzzmedia.imgscalr Scalr]
             [org.jets3t.service.security AWSCredentials]
             [org.jets3t.service.utils ServiceUtils]
             [org.jets3t.service.model S3Object]
             [org.jets3t.service.impl.rest.httpclient RestS3Service]))


(defn count-posts []
    (throw (Exception. "count-posts not yet done")))

(defn get-posts [number offset]
    (throw (Exception. "get-posts not yet done, but i may not need it")))
                    
(defn get-content-type
    "get the image content type and format; map gif to png, otherwise make a
    best effort to match the type"
    [full-image-content-type]
    (let [mime-types (ImageIO/getWriterMIMETypes)
          formats (ImageIO/getWriterFormatNames)
          desired-content-type (if (= "image/gif" full-image-content-type)
                                   "image/png"
                                   full-image-content-type)
          desired-format (last (split desired-content-type #"\/"))]
        (cond
            (and
                (some #(= desired-content-type %) mime-types)
                (some #(= desired-format %) formats))
            [desired-content-type desired-format]
            (and
                (some #(= "image/png" %) mime-types)
                (some #(= "png" %) formats))
            ["image/png" "png"]
            :else (throw (Exception.
                             (str "unknown mime type: " desired-content-type))))))

(defn -image-name [filename]
    (throw (Exception. "now this needs to do something based on the hash")))

(defn do-scale
    "scales the image, returns the bytes of the image"
    [imagestream full-img-content-type width userid]
    (let [fullimg (ImageIO/read imagestream)
          scaledimg (Scalr/resize fullimg width
                        (make-array BufferedImageOp 0))
          mimetype (get-content-type full-img-content-type)
          baos (ByteArrayOutputStream.)]
        (try
            (ImageIO/write scaledimg (last mimetype) baos)
            {:image-bytes (.toByteArray baos) :content-type (first mimetype)
             :owner userid}
            (finally (.close baos)))))

(defn scale-image-to-bytes
    [path full-img-content-type width userid]
    (let [is (FileInputStream. path)]
        (try
            (do-scale is full-img-content-type width userid)
            (finally (.close is)))))

(defn -do-image-upload
    "upload the image to s3, and return the id of the new s3reference row"
    [imgmap filename imageid res]
    (let [credentials (AWSCredentials. aws-access-key aws-secret-key)
          image-md5 (ServiceUtils/computeMD5Hash (:image-bytes imgmap))
          s3Service (RestS3Service. credentials)
          s3Bucket (.getBucket s3Service image-bucket)
          remote-filename (-image-name imageid filename res (:content-type imgmap))
          s3Object (S3Object. remote-filename (:image-bytes imgmap))]
        (.setContentType s3Object (:content-type imgmap))
        (.setMd5Hash s3Object image-md5)
        (.addMetadata s3Object "owner" (str (:owner imgmap)))
        (.putObject s3Service s3Bucket s3Object)))

(defn -get-image-bytes-from-s3 [filename]
    (let [credentials (AWSCredentials. aws-access-key aws-secret-key)
          s3Service (RestS3Service. credentials)
          s3Bucket (.getBucket s3Service image-bucket)
          s3Object (.getObject s3Service s3Bucket filename)]
        (.getDataInputStream s3Object)))
