(ns clj-thamil.format.analysis
  (:require [clojure.java.io :as jio]
            [clojure.data.csv :as csv]
            [clojure.string :as string])
  (:use [clj-thamil.format :as fmt :only [letters
                                          make-trie in-trie?
                                          str->elems]])
  (:gen-class))

(def letters-plus-grantha
  (concat letters 
          [["ஜ்" "ஜ" "ஜா" "ஜி" "ஜீ" "ஜு" "ஜூ" "ஜெ" "ஜே" "ஜை" "ஜொ" "ஜோ" "ஜௌ"]
           ["ஷ்" "ஷ" "ஷா" "ஷி" "ஷீ" "ஷு" "ஷூ" "ஷெ" "ஷே" "ஷை" "ஷொ" "ஷோ" "ஷௌ"]
           ["ஸ்" "ஸ" "ஸா" "ஸி" "ஸீ" "ஸு" "ஸூ" "ஸெ" "ஸே" "ஸை" "ஸொ" "ஸோ" "ஸௌ"]
           ["ஹ்" "ஹ" "ஹா" "ஹி" "ஹீ" "ஹு" "ஹூ" "ஹெ" "ஹே" "ஹை" "ஹொ" "ஹோ" "ஹௌ"]
           ["க்ஷ்" "க்ஷ" "க்ஷா" "க்ஷி" "க்ஷீ" "க்ஷு" "க்ஷூ" "க்ஷெ" "க்ஷே" "க்ஷை" "க்ஷொ" "க்ஷோ" "க்ஷௌ"]
           ["ஶ்ரீ"]]))

(defn trie-elem-freqs
  "given a trie of strings (char seqs) and an input string, return a frequency map for every letter in the trie appearing in the input string"
  [trie s]
  (let [keep-fn (fn [x]
                  (when (in-trie? trie x)
                    x))
        letters (str->elems trie s)
        letters-in-trie (keep keep-fn letters)]
    (frequencies letters-in-trie)))

(defn trie-elem-string-seq-freqs
  "given a trie of strings (char seqs) and a sequence of input strings, return a final frequency map for every letter appearing across all strings"
  [trie strs]
  (apply merge-with + (map (partial trie-elem-freqs trie) strs)))

(defn freq-grid
  "given a sequence of தமிழ் letters (flattened from a letter grid) and a map of those letters' frequences, return the frequencies in the shape of the grid"
  [letter-seq freq-map]
  (let [freq-seq (map #(or (get freq-map %) 0) letter-seq)
        freq-grid (partition-all 13 freq-seq)]
    freq-grid))

;;
;; printing functions
;;

(defn print-freq-grid
  "given a number grid and the corresponding letter grid, print them to std out"
  [freq-grid letter-grid]
  (let [print-grid (fn [grid] (doseq [row grid] (println (string/join "\t" row))))]
    (println "the letter grid's frequencies:")
    (println "")
    ;; (csv/write-csv *out* freq-grid :separator \tab :quote? false)
    (print-grid freq-grid)
    (println "")
    (println "the letter grid used:")
    ;; (csv/write-csv *out* letter-grid :separator \tab :quote? false)
    (print-grid letter-grid)))

(defn print-consonant-row-sums
  "given a frequnecy grid in the shape of a letter grid, and the letter grid itself, print out the sums of each consonant's row"
  [freq-grid letter-grid]
  (let [;; use rest in order to drop the first row = vowel row 
        row-names (rest (map first letter-grid))
        row-sums (rest (map (partial apply +) freq-grid))]
    (dorun
     (map (fn [rn rs] (println "For consonant:" rn ", there are" rs "instances of it in a C or C+V letter")) row-names row-sums))))

(defn print-vowel-col-sums
  "given a frequency gridn in the shape of a letter grid, and the letter grid itself, print out the sums of each vowel's column specifically among consonant and consonant+vowel letters (exclude pure vowels)"
  [freq-grid letter-grid]
  (let [full-row-freq-grid (->> freq-grid
                                rest
                                (filter #(= 13 (count %))))
        full-row-letter-grid (->> letter-grid
                                  rest
                                  (filter #(= 13 (count %))))
        vowels (first letter-grid)
        col-names vowels
        freq-cols (apply map list full-row-freq-grid)
        col-sums (map (partial apply +) freq-cols)]
    (dorun
     (map (fn [cn cs] (println "For vowel/ஃ:" cn ", there are" cs "instances of it in a C or C+V letter")) col-names col-sums))))

;;
;; umbrella printing fn
;;

(defn print-letter-grid-stats-on-strs
  "for a given letter grid and a sequence of strings, print out all of the stats"
  [letter-grid strs]
  (let [letter-seq (flatten letter-grid)
        letter-trie (make-trie letter-seq)
        str->letters (fn [s] (str->elems letter-trie s))
        fmap (trie-elem-string-seq-freqs letter-trie strs)
        fgrid (freq-grid letter-seq fmap)] 
    (print-vowel-col-sums fgrid letter-grid)
    (println "")
    (print-consonant-row-sums fgrid letter-grid)
    (println "")
    (print-freq-grid fgrid letter-grid)))

;;
;; main fn
;;

(defn -main
  [& args]
  (with-open [rdr (jio/reader *in*)]
    (let [lines (line-seq rdr)
          letter-grid letters-plus-grantha]
      (print-letter-grid-stats-on-strs letter-grid lines))))
