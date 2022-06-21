(ns xlfe.tlprt.core

  #?(:cljs 
      (:require
              [clojure.walk]
              [clojure.string]
              [cognitect.transit :as t]
              [cljsjs.pako]
              [goog.crypt :as gc]
              [goog.crypt.base64 :as b64]))

    #?(:clj 
        (:require
              [clojure.walk]
              [clojure.string]
              [cognitect.transit :as t]
              [zlib-tiny.core :as zlib]))

    #?(:clj 
        (:import 
          java.util.Base64
          (org.apache.commons.io IOUtils)
          (java.util.zip DeflaterInputStream Deflater)
          (java.io ByteArrayInputStream ByteArrayOutputStream))))
         

;we use Base64 with a slight modification that the resulting string should be safe for 
; browser URIs or DOM identifiers... so it :-
;
; * must begin with a letter ([A-Za-z]) and may be followed by 
; * any number of letters, digits ([0-9]), hyphens ("-"), underscores ("_"), colons (":"), and periods (".")

(def ->translate {"/" "-" "=" "_" "+" "."})
(def <-translate {"-" "/" "_" "=" "." "+"})

(defn- str->safer [s] (str "t" (clojure.string/replace s #"[/=+]" ->translate)))
(defn- safer->str [s] (clojure.string/replace (subs s 1) #"[\-_\.]" <-translate))

(defn- zlib-inflate
  [z]
  #?(:cljs (.inflateRaw js/pako z #js{:to "string"})
     :clj  (zlib/inflate z)))
      
(defn- zlib-deflate
  [z]
  #?(:cljs (.deflateRaw js/pako z #js{:level 9})
     :clj  (let [deflater (Deflater. 9 true)
                 ba (IOUtils/toByteArray (DeflaterInputStream. (ByteArrayInputStream. z)
                                                               deflater))]
             (.end deflater)
             ba)))
     
(defn byte-array->tlprt-string
  "convert a byte-array to a tlprt encoded string"
  [ba]
  #?(:cljs (str->safer (b64/encodeByteArray ba))
     :clj  (str->safer (.encodeToString (java.util.Base64/getEncoder) ba))))

(defn tlprt-string->byte-array
  "convert a tlprt-string and decode it to its original byte-array"
  [s]
  #?(:cljs (b64/decodeStringToByteArray (safer->str s))
     :clj  (.decode (Base64/getDecoder) (safer->str s))))


(defn string->tlprt-string
 "convert a string to a tlprt-string"
 [s]
 ;convert strings to their byte array
 (byte-array->tlprt-string
   #?(:cljs (gc/stringToUtf8ByteArray s)
      :clj  (.getBytes s))))

(defn tlprt-string->string
  "convert a tlprt-string back to its original string"
  [s]
  #?(:cljs (gc/utf8ByteArrayToString (tlprt-string->byte-array s))
     :clj  (String.                  (tlprt-string->byte-array s))))




(defn transit-object->tlprt-string
  "convert any clojure structure that can be represented by transit into a tlprt-string"
  [s]
  #?(:cljs
       (byte-array->tlprt-string (zlib-deflate (t/write (t/writer :json) s)))

     :clj
     (let [out (ByteArrayOutputStream.)
           writer (t/writer out :json)]
       (t/write writer s)
       (byte-array->tlprt-string (zlib-deflate (.toByteArray out)))))) 

(defn tlprt-string->transit-object
  "take a previously-encoded tlprt-string and decode it back (via transit) into its origina clojure data"
  [s]
  #?(:cljs (t/read (t/reader :json) (zlib-inflate (tlprt-string->byte-array s)))
     :clj (->
            (tlprt-string->byte-array s)
            (ByteArrayInputStream.) 
            zlib-inflate
            (t/reader :json)
            (t/read)))) 
