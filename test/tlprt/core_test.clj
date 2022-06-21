(ns tlprt.core-test
  (:require 
    [expectations.clojure.test :refer [defexpect expect]]
    [tlprt.shared :as s]))

(defexpect test-transit-zlib-roundtrips
  (expect
    (true? (s/test-one)))

  (expect
    (true? (s/test-two)))

  (expect
    (true? (s/test-three)))

  (expect
    (true? (s/test-strings))))
