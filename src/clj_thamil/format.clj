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

(defn- trie-add-seq
  "take a trie (represented as a nested map) and add a sequence"
  [trie-map s] 
  (loop [idx (count s)
         tm trie-map]
    (when-not (neg? idx)
      (if (zero? idx)
        (if (= 1 (count s))
          (assoc-in tm s {nil nil})
          (update-in tm (vec s) assoc-in [nil] nil))
        (let [[pre post] (split-at idx s)] 
          (if (get-in tm pre)
            (update-in tm pre assoc-in (concat post [nil]) nil)
            (recur (dec idx) tm)))))))

(defn- make-trie
  "take a sequence (may be nested) of input sequences and creates a trie, represented as a nested map"
  [sequence]
  (let [s (flatten sequence)]
    (reduce trie-add-seq {} s)))

(defn- trie-find
  "take a trie and a sequence and look up the sequence in the trie"
  [trie sq]
  (get-in trie sq))

(def ^{:private true
       :doc "a trie that converts a string of characters/codepoints into strings representing the individual letters in தமிழ்"}
  codepoint-trie (make-trie letters))

(defn str->letters
  "take a string and split it into its constitutent தமிழ் + non-complex letters (non-complex = all left-to-right, 1-to-1 codepoint-to-glyph encodings -- this includes all Western languages)"
  [s] 
  (loop [idx 0
         new-chars []
         letters []]
    (if (= idx (count s))
      (if (empty? new-chars)
        letters
        (conj letters (apply str new-chars)))
      (let [next-char (.charAt s idx)]
        (if (nil? (trie-find codepoint-trie (apply str (conj new-chars next-char))))
          (if (empty? new-chars)
            (recur (inc idx) (conj new-chars next-char) letters)
            (recur (inc idx) [next-char] (conj letters (apply str new-chars))))
          (recur (inc idx) (conj new-chars next-char) letters))))))

(def ^{:private true
       :doc "a flattened seq of all தமிழ் letters in lexicographical (alphabetical) order -- put anohter way, in the order of அகர முதல் னரக இறுவாய் as the 2500 yr old grammatical compendium தொல்காப்பியம் states in its outset"}
  sort-order (flatten (concat vowels consonants)))

(def ^{:doc "a map where the key is a தமிழ் letter, and the value is a number indicating its relative position in sort order"}
  sort-map (zipmap sort-order (range)))

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

(defn word-under-cursor
  "given a string and an index number that the cursor is on or before, return the word that the cursor is in the middle of. if cursor is before or after a word, or at the beginning or end of string, return a falsey value (ex: nil).  accepts idx being at end of string (idx == (count s))"
  [s idx]
  (assert (<= 0 idx) (str "cursor postiion out of range [idx =" idx "]"))
  (assert (<= idx (count s)) (str "cursor postiion out of range [idx =" idx "], [str len =" (count s) "]"))
  ;; TODO: handle case where cursor is at end of string
  (let [[before after] (split-at idx s)]
    (cond
     (and (re-seq #".*\b\W*" before) (re-seq #"\w+.*" after))
     (first (re-seq #"(\w+).*" after))
     true
     nil)))
