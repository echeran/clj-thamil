(ns clj-thamil.format.analysis-test
  (:require [clj-thamil.format :as fmt])
  (:use clj-thamil.format.analysis 
        clojure.test))

(deftest letters-plus-grantha-test
  (let [letters-plus-grantha-trie (fmt/make-trie (flatten letters-plus-grantha))
        str->letters-plus-grantha (fn [s] (fmt/str->elems letters-plus-grantha-trie s))]
    (testing "string fns also working on grantha letters"
      (is (= ["ஜி" "மி" "க்" "கி"] (str->letters-plus-grantha "ஜிமிக்கி")))
      (is (= ["கு" "ஷி"] (str->letters-plus-grantha "குஷி"))))
    (testing "trie-elem-freqs"
      (let [s "ஜோடி"]
        (is (= (trie-elem-freqs letters-plus-grantha-trie s)
               (trie-elem-freqs letters-plus-grantha-trie (str s " abc 123 a3"))))))))
