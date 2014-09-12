(ns clj-thamil.format
  (:use clj-thamil.core))

(def letters [["ஃ" "அ" "ஆ" "இ" "ஈ" "உ" "ஊ" "எ" "ஏ" "ஐ" "ஒ" "ஓ" "ஔ"]
              ["க்" "க" "கா" "கி" "கீ" "கு" "கூ" "கெ" "கே" "கை" "கொ" "கோ" "கௌ"]
              ["ங்" "ங" "ஙா" "ஙி" "ஙீ" "ஙு" "ஙூ" "ஙெ" "ஙே" "ஙை" "ஙொ" "ஙோ" "ஙௌ"]
              ["ச்" "ச" "சா" "சி" "சீ" "சு" "சூ" "செ" "சே" "சை" "சொ" "சோ" "சௌ"]
              ["ஞ்" "ஞ" "ஞா" "ஞி" "ஞீ" "ஞு" "ஞூ" "ஞெ" "ஞே" "ஞை" "ஞொ" "ஞோ" "ஞௌ"]
              ["ட்" "ட" "டா" "டி" "டீ" "டு" "டூ" "டெ" "டே" "டை" "டொ" "டோ" "டௌ"]
              ["ண்" "ண" "ணா" "ணி" "ணீ" "ணு" "ணூ" "ணெ" "ணே" "ணை" "ணொ" "ணோ" "ணௌ"]
              ["த்" "த" "தா" "தி" "தீ" "து" "தூ" "தெ" "தே" "தை" "தொ" "தோ" "தௌ"]
              ["ந்" "ந" "நா" "நி" "நீ" "நு" "நூ" "நெ" "நே" "நை" "நொ" "நோ" "நௌ"]
              ["ப்" "ப" "பா" "பி" "பீ" "பு" "பூ" "பெ" "பே" "பை" "பொ" "போ" "பௌ"]
              ["ம்" "ம" "மா" "மி" "மீ" "மு" "மூ" "மெ" "மே" "மை" "மொ" "மோ" "மௌ"]
              ["ய்" "ய" "யா" "யி" "யீ" "யு" "யூ" "யெ" "யே" "யை" "யொ" "யோ" "யௌ"]
              ["ர்" "ர" "ரா" "ரி" "ரீ" "ரு" "ரூ" "ரெ" "ரே" "ரை" "ரொ" "ரோ" "ரௌ"]
              ["ல்" "ல" "லா" "லி" "லீ" "லு" "லூ" "லெ" "லே" "லை" "லொ" "லோ" "லௌ"]
              ["வ்" "வ" "வா" "வி" "வீ" "வு" "வூ" "வெ" "வே" "வை" "வொ" "வோ" "வௌ"]
              ["ழ்" "ழ" "ழா" "ழி" "ழீ" "ழு" "ழூ" "ழெ" "ழே" "ழை" "ழொ" "ழோ" "ழௌ"]
              ["ள்" "ள" "ளா" "ளி" "ளீ" "ளு" "ளூ" "ளெ" "ளே" "ளை" "ளொ" "ளோ" "ளௌ"]
              ["ற்" "ற" "றா" "றி" "றீ" "று" "றூ" "றெ" "றே" "றை" "றொ" "றோ" "றௌ"]
              ["ன்" "ன" "னா" "னி" "னீ" "னு" "னூ" "னெ" "னே" "னை" "னொ" "னோ" "னௌ"]])

(def vowels
  (let [vowel-row (first letters)]
    (concat (rest vowel-row) [(first vowel-row)])))

(def consonants (rest letters))

;;;;;;;;;;;
;; trie fns
;;;;;;;;;;;

(defn- trie-add-seq
  "take a trie (represented as a nested map) and add a sequence, with an optional value attached to its terminus"
  ([trie-map s]
     (trie-add-seq trie-map s nil))
  ([trie-map s term-val] 
     (loop [idx (count s)
            tm trie-map]
       (when-not (neg? idx)
         (if (zero? idx)
           (if (= 1 (count s))
             (assoc-in tm s {nil term-val})
             (update-in tm (vec s) assoc-in [nil] term-val))
           (let [[pre post] (split-at idx s)] 
             (if (get-in tm pre)
               (update-in tm pre assoc-in (concat post [nil]) term-val)
               (recur (dec idx) tm))))))))

(defn make-trie
  "take a sequence (may be nested) of input sequences, or else takes a map (single-level) where keys are sequences and vals are attached to the terminus in trie. fn creates a trie, represented as a nested map."
  [sequence]
  (if (map? sequence)
    (reduce (partial apply trie-add-seq) {} sequence)
    (let [s (flatten sequence)]
      (reduce trie-add-seq {} s))))

(defn trie-prefix-subtree
  "take a trie and a sequence, look up the sequence in the trie, and return the subtree"
  [trie sq]
  (get-in trie sq))

(defn in-trie?
  "return whether the sequence exists in the trie"
  [trie sq]
  (-> (trie-prefix-subtree trie sq)
      (find nil)
      boolean))

(def ^{:private true
       :doc "a trie that converts a string of characters/codepoints into strings representing the individual letters in தமிழ்"}
  codepoint-trie (make-trie letters))

(defn str->letters
  "take a string and split it into its constitutent தமிழ் + non-complex letters (non-complex = all left-to-right, 1-to-1 codepoint-to-glyph encodings -- this includes all Western languages)"
  ([s]
     (str->letters codepoint-trie s))
  ([trie s]
     (loop [idx 0
            new-chars []
            letters []]
       (if (= idx (count s))
         (if (empty? new-chars)
           letters
           (conj letters (apply str new-chars)))
         (let [next-char (.charAt s idx)]
           (if (nil? (trie-prefix-subtree trie (apply str (conj new-chars next-char))))
             (if (empty? new-chars)
               (recur (inc idx) (conj new-chars next-char) letters)
               (recur (inc idx) [next-char] (conj letters (apply str new-chars))))
             (recur (inc idx) (conj new-chars next-char) letters)))))))

;;;;;;;;;;;;;;
;; sorting fns
;;;;;;;;;;;;;; 

(def ^{:private true
       :doc "a flattened seq of all தமிழ் letters in lexicographical (alphabetical) order -- put anohter way, in the order of அகர முதல் னரக இறுவாய் as the 2500 yr old grammatical compendium தொல்காப்பியம் states in its outset"}
  letter-seq (flatten (concat vowels consonants)))

(def ^{:doc "a map where the key is a தமிழ் letter, and the value is a number indicating its relative position in sort order"}
  sort-map (zipmap letter-seq (range)))

(defn letter-before?
  "a 2-arg predicate indicating whether the first string comes before the second string, but assuming that each string will only represent individual letters"
  [s1 s2]
  (cond (and (nil? s1) (nil? s2)) true
        (and (nil? (get sort-map s1)) (nil? (get sort-map s2))) (boolean (neg? (compare s1 s2)))
        (nil? (get sort-map s1)) true
        (nil? (get sort-map s2)) false
        :else (< (get sort-map s1) (get sort-map s2))))

(def ^{:doc "a comparator for strings that represent a single letter that respects தமிழ் alphabetical order"}
  letter-comp (comparator letter-before?))

(defn word-before?
  "a 2-arg predicate indicating whether the first string comes before the second string lexicographically, handling தமிழ் letters in addition to 1-to-1 codepoint-to-letter encodings"
  [str1 str2]
  (loop [s1 (str->letters str1)
         s2 (str->letters str2)]
    (cond (not (seq s1)) (boolean (seq s2))
          (not (seq s2)) false 
          (not= (first s1) (first s2)) (letter-before? (first s1) (first s2))
          :else (recur (rest s1) (rest s2)))))

(def ^{:private true
       :doc "a comparator for lexicographical comparisons of arbitrary strings (consisting of தமிழ் letters and letters from 1-to-1 encodings)"}
  word-comp (comparator word-before?))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; word & character traits fns
;; position fns
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn whitespace?
  "returns whether a Java Character a.k.a. Unicode codepoint is whitespace or not (according to Java's understanding of Unicode)"
  [ch]
  (when ch
    (Character/isWhitespace ch)))

(defn wordy-char?
  "take a Java Character a.k.a. Unicode codepoint and return whether it represents a character that might go into a word or identifier.  In other words, it is for Unicode like what \\w has representing in regular expressions for ASCII characters -- which is alpha-numeric characters"
  [ch]
  (when ch
    (and
     (not (get #{\$ \_} ch))
     (Character/isJavaIdentifierPart ch))))

(defn wordy-seq
  "take a string and produce a seq of the Unicode-aware version of the \\w+ regex pattern - basically, split input string into all chunks of non-whitepsace.  Originally, I called this fn word-seq, but that is not true for all languages and/or throughout time where there was no spearation between words (ex: Thai, Chinese, Japanese, Latin manuscripts, ancient Thamil stone inscriptions, etc.)"
  [s]
  (when s
    (let [chunks (partition-by wordy-char? s)
          word-chunks (filter (comp wordy-char? first) chunks)
          words (map (partial apply str) word-chunks)]
      words)))

(defn wordy-chunk-and-cursor-pos
  "given a string and an index number that the cursor is on or before, return the wordy chunk that the cursor is in the middle of, and the cursor pos relative to the chunk. if cursor is before or after a word, or at the beginning or end of string, return a falsey value (ex: nil).  accepts idx being at end of string (idx == (count s))."
  [s idx]
  (assert (<= 0 idx) (str "cursor postiion out of range [idx =" idx "]"))
  (assert (<= idx (count s)) (str "cursor postiion out of range [idx =" idx "], [str len =" (count s) "]"))
  ;; TODO: handle case where cursor is at end of string
  (let [[before after] [(subs s 0 idx) (subs s idx)]
        partitions-before (partition-by wordy-char? before)
        partitions-after (partition-by wordy-char? after)
        wordy-chunks-before (wordy-seq before)
        wordy-chunks-after (wordy-seq after)
        chunk-seq-wordy? (comp wordy-char? first)
        prev-chunk (last wordy-chunks-before)
        next-chunk (first wordy-chunks-after) 
        prev-chunk-wordiness (chunk-seq-wordy? (last partitions-before))
        next-chunk-wordiness (chunk-seq-wordy? (first partitions-after))
        prev-chunk-idx (if prev-chunk (.indexOf before prev-chunk) -1)
        next-chunk-idx (if next-chunk (.indexOf after next-chunk) -1)
        prev-chunk-flush (= idx (+ prev-chunk-idx (count prev-chunk)))
        next-chunk-flush (zero? next-chunk-idx)]
    (cond
     (and prev-chunk-wordiness next-chunk-wordiness prev-chunk-flush next-chunk-flush) [(str prev-chunk next-chunk) (- idx prev-chunk-idx)]
     (and prev-chunk-wordiness prev-chunk-flush) [prev-chunk (- idx prev-chunk-idx)]
     (and  next-chunk-wordiness next-chunk-flush) [next-chunk 0]
     :else nil)))

(def wordy-chunk-under (comp first wordy-chunk-and-cursor-pos))


