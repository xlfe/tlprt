(ns tlprt.core-test
  (:require 
    [cljs.test :refer [deftest testing is]]
    [tlprt.shared :as s]))

(deftest test-transit-zlib-roundtrips
  (testing "one round"
    (is (true? (s/test-one))))

  (testing "multi-rounds"
    (is (true? (s/test-two))))

  (testing "another single"
    (is (true? (s/test-three))))

  (testing "strings"
    (is (true? (s/test-strings)))))
