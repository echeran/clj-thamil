(ns clj-thamil.format.analys-test
  (:use clj-thamil.format.analysis
        [clj-thamil.format :as fmt :only [letters consonants
                                          make-trie in-trie?
                                          str->elems str->letters str->phonemes phoneme-map]]
        clojure.test))

(deftest letters-plus-grantha-test 
  (testing "string fns also working on grantha letters"
    (is (= ["ஜி" "மி" "க்" "கி"] (str->letters-plus-grantha "ஜிமிக்கி")))
    (is (= ["கு" "ஷி"] (str->letters-plus-grantha "குஷி"))))
  (testing "trie-elem-freqs"
    (let [s "ஜோடி"]
      (is (= (trie-elem-freqs letters-plus-grantha-trie s)
             (trie-elem-freqs letters-plus-grantha-trie (str s " abc 123 a3")))))))
