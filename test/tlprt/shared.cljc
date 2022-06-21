(ns tlprt.shared
  (:require 
    [xlfe.tlprt.core :as t]))

(def struct-input
  {:a [:b]
      :b [:a :c :d]
         :c [:b :e]
         :d [:b :c :e]
         :e [:c :d :f]
         :f []})

(def struct-output
  [[:a :b :c :e] 
   [:a :b :d :e]])

(defn test-one
  []
  (=
    (t/tlprt-string->transit-object "si1aKU1DSUaqzys1PKc1JBTNLEpOgrMwUJZ280pycWAA_")
    {:module :table :id nil}))

(defn test-two
  []
  (=
    (t/tlprt-string->transit-object "si1aKU1DSUaqzSlTSiQZSSUqxOmAKzEsESyWDyRSITDJMHYiTChFLQRZLRpZJhcjATACRaRCZNKBMbCwA")
    (t/tlprt-string->transit-object (t/transit-object->tlprt-string struct-input))
    struct-input))

(defn test-three
  []
  (=
    (t/tlprt-string->transit-object (t/transit-object->tlprt-string struct-output))
    struct-output))

(def test-string 
  (str "prøve" "mañana" "große" "plaît" "日本語" "の" "文章" "に" "は" "スペース" "が" "必要" "ない" "って" "本当"))


;; should be the same in cljs and clj
(def encoded-string 
  "scHLDuHZlbWHDsWFuYWdyb8OfZXBsYcOudOaXpeacrOiqnuOBruaWh.eroOOBq.OBr.OCueODmuODvOOCueOBjOW-heimgeOBquOBhOOBo.OBpuacrOW9kw__")

(defn test-strings
  []
  (= test-string 
     (t/tlprt-string->string encoded-string)
     (t/tlprt-string->string
       (t/string->tlprt-string test-string))))
