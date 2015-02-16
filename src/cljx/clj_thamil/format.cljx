(ns clj-thamil.format
  (:require [clojure.set :as set])
  (:use clj-thamil.core))

;;;;;;;;;;
;; letters
;;;;;;;;;;

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

(def c-cv-letters (rest letters))

(def consonants (map first c-cv-letters))

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

(def ^{:private true
       :doc "a trie that contains all strings representing the individual letters in தமிழ்"}
  letter-trie (make-trie letters))

(defn trie-prefix-subtree
  "take a trie and a sequence, look up the sequence in the trie, and return the subtree"
  [trie sq]
  (get-in trie sq))

(defn in-trie?
  "return whether the sequence exists in the trie"
  ([sq]
     (in-trie? letter-trie sq))
  ([trie sq]
     (-> (trie-prefix-subtree trie sq)
         (find nil)
         boolean)))

(defn get-in-trie
  "return the corresponding value from the trie -- either the combined version of the input seq, or the value attached to the terminus of the input seq in the trie"
  [trie sq] 
  (if (in-trie? trie sq)
    (let [subtree (trie-prefix-subtree trie sq)]
      (if (nil? (get subtree nil))
        (apply str sq)
        (get subtree nil)))
    (apply str sq)))

(defn- backfill-new-chars
  "a helper fn for str->elems that takes the new-chars array (after knowing that the next character cannot be added to it because the resultant char path would not be in the trie) as input. we now need to process the new-chars array to test whether it (or else, its substrings) are themselves in the trie.  we need to work backwards to find the maximally long substring (char seq) that is also in trie.
  this fn is set up as O(n^2) on the assumption that input sequences won't be too big (the sequences that make up the paths of the trie don't have too many shared long sequences that start at the trie root).
  this fn might be needed to distinguish, for example, between a 3-elem chunk and 2 smaller chnks (ex: \"ksh\" vs \"k\" + \"sh\" -- ignore the fact that க்ஷ் and ஸ் aren't originally Thamil).  in fact, this fn probably isn't necessary for original Thamil letters, since they only need 2 codepoints, and may be only an issue for English transliteration of Grantha letters, or more of an issue for others languages which require 3+ chars to form a letter)"
  [trie new-chars & [{:keys [flat-output] :as opts}]]
  (loop [chars new-chars
         in-trie-letters []
         idx (count chars)]
    (condp = idx
      0 (if-not flat-output (flatten in-trie-letters) in-trie-letters)
      1 (recur (drop 1 chars) (conj in-trie-letters (get-in-trie trie (take 1 chars))) (count (drop 1 chars)))
      ;; else
      (if (in-trie? trie (take idx chars))
        (recur (drop idx chars) (conj in-trie-letters (get-in-trie trie (take idx chars))) (count (drop idx chars)))
        (recur chars in-trie-letters (dec idx))))))

(defn str->elems
  "take a string and split it into chunks based on the input trie.  for every maximally long sequence in the trie that is detected in the input string, the terminus-attached value is added to the output sequence if it exists (ex: useful for transliteration / format conversion), or else the string chunk itself is added."
  ([s]
     (str->elems letter-trie s))
  ([trie s & [{:keys [transform] :as opts}]]
     ;; loop is like a procedural for loop or while loop
     ;; this loop is like a for loop, where 0 <= idx < (count s)
     (loop [idx 0
            new-chars []
            letters []]
       ;; test if we've consumed our entire input string
       (if (= idx (count s))
         ;; test whether we have handled entire input string, or if
         ;; there are still chars still not fully processed
         (if (empty? new-chars)
           letters
           (concat letters (backfill-new-chars trie new-chars)))
         ;; start next iteration
         (let [next-char (.charAt s idx)]
           ;; if adding the next character makes a prefix in trie no
           ;; longer in trie, then we have our maximally long prefix.
           ;; if not, just add the char and continue
           (if (nil? (trie-prefix-subtree trie (apply str (conj new-chars next-char))))
             ;; test whether this is just because we're at the
             ;; beginning of our string.  if not, return our prefix
             ;; and reset our next prefix starting with the new char
             (if (empty? new-chars)
               (recur (inc idx) (conj new-chars next-char) letters)
               (recur (inc idx) [next-char] (concat letters (backfill-new-chars trie new-chars))))
             (recur (inc idx) (conj new-chars next-char) letters)))))))

;;;;;;;;;;;
;; letters & phonemes
;;;;;;;;;;;

(defn str->letters
  "take a string and split it into its constitutent தமிழ் + non-complex letters (non-complex = all left-to-right, 1-to-1 codepoint-to-glyph encodings -- this includes all Western languages)"
  [s]
  (str->elems letter-trie s))

(def ^{:doc "a map whose keys are தமிழ் letters and whose values are sequences of the constituent phonemes (represented as strings) of those letters. letters are from the set {உயிர்-, மெய்-, உயிர்மெய்-}எழுத்துகள், phonemes are from the set {உயிர்-,மெய்-}எழுத்துகள்"}
  phoneme-map
  {"ஃ" ["ஃ"],
   "அ" ["அ"],
   "ஆ" ["ஆ"],
   "இ" ["இ"],
   "ஈ" ["ஈ"],
   "உ" ["உ"],
   "ஊ" ["ஊ"],
   "எ" ["எ"],
   "ஏ" ["ஏ"],
   "ஐ" ["ஐ"],
   "ஒ" ["ஒ"],
   "ஓ" ["ஓ"],
   "ஔ" ["ஔ"],
   "க்" ["க்"],
   "க" ["க்" "அ"],
   "கா" ["க்" "ஆ"],
   "கி" ["க்" "இ"],
   "கீ" ["க்" "ஈ"],
   "கு" ["க்" "உ"],
   "கூ" ["க்" "ஊ"],
   "கெ" ["க்" "எ"],
   "கே" ["க்" "ஏ"],
   "கை" ["க்" "ஐ"],
   "கொ" ["க்" "ஒ"],
   "கோ" ["க்" "ஓ"],
   "கௌ" ["க்" "ஔ"],
   "ங்" ["ங்"],
   "ங" ["ங்" "அ"],
   "ஙா" ["ங்" "ஆ"],
   "ஙி" ["ங்" "இ"],
   "ஙீ" ["ங்" "ஈ"],
   "ஙு" ["ங்" "உ"],
   "ஙூ" ["ங்" "ஊ"],
   "ஙெ" ["ங்" "எ"],
   "ஙே" ["ங்" "ஏ"],
   "ஙை" ["ங்" "ஐ"],
   "ஙொ" ["ங்" "ஒ"],
   "ஙோ" ["ங்" "ஓ"],
   "ஙௌ" ["ங்" "ஔ"],
   "ச்" ["ச்"],
   "ச" ["ச்" "அ"],
   "சா" ["ச்" "ஆ"],
   "சி" ["ச்" "இ"],
   "சீ" ["ச்" "ஈ"],
   "சு" ["ச்" "உ"],
   "சூ" ["ச்" "ஊ"],
   "செ" ["ச்" "எ"],
   "சே" ["ச்" "ஏ"],
   "சை" ["ச்" "ஐ"],
   "சொ" ["ச்" "ஒ"],
   "சோ" ["ச்" "ஓ"],
   "சௌ" ["ச்" "ஔ"],
   "ஞ்" ["ஞ்"],
   "ஞ" ["ஞ்" "அ"],
   "ஞா" ["ஞ்" "ஆ"],
   "ஞி" ["ஞ்" "இ"],
   "ஞீ" ["ஞ்" "ஈ"],
   "ஞு" ["ஞ்" "உ"],
   "ஞூ" ["ஞ்" "ஊ"],
   "ஞெ" ["ஞ்" "எ"],
   "ஞே" ["ஞ்" "ஏ"],
   "ஞை" ["ஞ்" "ஐ"],
   "ஞொ" ["ஞ்" "ஒ"],
   "ஞோ" ["ஞ்" "ஓ"],
   "ஞௌ" ["ஞ்" "ஔ"],
   "ட்" ["ட்"],
   "ட" ["ட்" "அ"],
   "டா" ["ட்" "ஆ"],
   "டி" ["ட்" "இ"],
   "டீ" ["ட்" "ஈ"],
   "டு" ["ட்" "உ"],
   "டூ" ["ட்" "ஊ"],
   "டெ" ["ட்" "எ"],
   "டே" ["ட்" "ஏ"],
   "டை" ["ட்" "ஐ"],
   "டொ" ["ட்" "ஒ"],
   "டோ" ["ட்" "ஓ"],
   "டௌ" ["ட்" "ஔ"],
   "ண்" ["ண்"],
   "ண" ["ண்" "அ"],
   "ணா" ["ண்" "ஆ"],
   "ணி" ["ண்" "இ"],
   "ணீ" ["ண்" "ஈ"],
   "ணு" ["ண்" "உ"],
   "ணூ" ["ண்" "ஊ"],
   "ணெ" ["ண்" "எ"],
   "ணே" ["ண்" "ஏ"],
   "ணை" ["ண்" "ஐ"],
   "ணொ" ["ண்" "ஒ"],
   "ணோ" ["ண்" "ஓ"],
   "ணௌ" ["ண்" "ஔ"],
   "த்" ["த்"],
   "த" ["த்" "அ"],
   "தா" ["த்" "ஆ"],
   "தி" ["த்" "இ"],
   "தீ" ["த்" "ஈ"],
   "து" ["த்" "உ"],
   "தூ" ["த்" "ஊ"],
   "தெ" ["த்" "எ"],
   "தே" ["த்" "ஏ"],
   "தை" ["த்" "ஐ"],
   "தொ" ["த்" "ஒ"],
   "தோ" ["த்" "ஓ"],
   "தௌ" ["த்" "ஔ"],
   "ந்" ["ந்"],
   "ந" ["ந்" "அ"],
   "நா" ["ந்" "ஆ"],
   "நி" ["ந்" "இ"],
   "நீ" ["ந்" "ஈ"],
   "நு" ["ந்" "உ"],
   "நூ" ["ந்" "ஊ"],
   "நெ" ["ந்" "எ"],
   "நே" ["ந்" "ஏ"],
   "நை" ["ந்" "ஐ"],
   "நொ" ["ந்" "ஒ"],
   "நோ" ["ந்" "ஓ"],
   "நௌ" ["ந்" "ஔ"],
   "ப்" ["ப்"],
   "ப" ["ப்" "அ"],
   "பா" ["ப்" "ஆ"],
   "பி" ["ப்" "இ"],
   "பீ" ["ப்" "ஈ"],
   "பு" ["ப்" "உ"],
   "பூ" ["ப்" "ஊ"],
   "பெ" ["ப்" "எ"],
   "பே" ["ப்" "ஏ"],
   "பை" ["ப்" "ஐ"],
   "பொ" ["ப்" "ஒ"],
   "போ" ["ப்" "ஓ"],
   "பௌ" ["ப்" "ஔ"],
   "ம்" ["ம்"],
   "ம" ["ம்" "அ"],
   "மா" ["ம்" "ஆ"],
   "மி" ["ம்" "இ"],
   "மீ" ["ம்" "ஈ"],
   "மு" ["ம்" "உ"],
   "மூ" ["ம்" "ஊ"],
   "மெ" ["ம்" "எ"],
   "மே" ["ம்" "ஏ"],
   "மை" ["ம்" "ஐ"],
   "மொ" ["ம்" "ஒ"],
   "மோ" ["ம்" "ஓ"],
   "மௌ" ["ம்" "ஔ"],
   "ய்" ["ய்"],
   "ய" ["ய்" "அ"],
   "யா" ["ய்" "ஆ"],
   "யி" ["ய்" "இ"],
   "யீ" ["ய்" "ஈ"],
   "யு" ["ய்" "உ"],
   "யூ" ["ய்" "ஊ"],
   "யெ" ["ய்" "எ"],
   "யே" ["ய்" "ஏ"],
   "யை" ["ய்" "ஐ"],
   "யொ" ["ய்" "ஒ"],
   "யோ" ["ய்" "ஓ"],
   "யௌ" ["ய்" "ஔ"],
   "ர்" ["ர்"],
   "ர" ["ர்" "அ"],
   "ரா" ["ர்" "ஆ"],
   "ரி" ["ர்" "இ"],
   "ரீ" ["ர்" "ஈ"],
   "ரு" ["ர்" "உ"],
   "ரூ" ["ர்" "ஊ"],
   "ரெ" ["ர்" "எ"],
   "ரே" ["ர்" "ஏ"],
   "ரை" ["ர்" "ஐ"],
   "ரொ" ["ர்" "ஒ"],
   "ரோ" ["ர்" "ஓ"],
   "ரௌ" ["ர்" "ஔ"],
   "ல்" ["ல்"],
   "ல" ["ல்" "அ"],
   "லா" ["ல்" "ஆ"],
   "லி" ["ல்" "இ"],
   "லீ" ["ல்" "ஈ"],
   "லு" ["ல்" "உ"],
   "லூ" ["ல்" "ஊ"],
   "லெ" ["ல்" "எ"],
   "லே" ["ல்" "ஏ"],
   "லை" ["ல்" "ஐ"],
   "லொ" ["ல்" "ஒ"],
   "லோ" ["ல்" "ஓ"],
   "லௌ" ["ல்" "ஔ"],
   "வ்" ["வ்"],
   "வ" ["வ்" "அ"],
   "வா" ["வ்" "ஆ"],
   "வி" ["வ்" "இ"],
   "வீ" ["வ்" "ஈ"],
   "வு" ["வ்" "உ"],
   "வூ" ["வ்" "ஊ"],
   "வெ" ["வ்" "எ"],
   "வே" ["வ்" "ஏ"],
   "வை" ["வ்" "ஐ"],
   "வொ" ["வ்" "ஒ"],
   "வோ" ["வ்" "ஓ"],
   "வௌ" ["வ்" "ஔ"],
   "ழ்" ["ழ்"],
   "ழ" ["ழ்" "அ"],
   "ழா" ["ழ்" "ஆ"],
   "ழி" ["ழ்" "இ"],
   "ழீ" ["ழ்" "ஈ"],
   "ழு" ["ழ்" "உ"],
   "ழூ" ["ழ்" "ஊ"],
   "ழெ" ["ழ்" "எ"],
   "ழே" ["ழ்" "ஏ"],
   "ழை" ["ழ்" "ஐ"],
   "ழொ" ["ழ்" "ஒ"],
   "ழோ" ["ழ்" "ஓ"],
   "ழௌ" ["ழ்" "ஔ"],
   "ள்" ["ள்"],
   "ள" ["ள்" "அ"],
   "ளா" ["ள்" "ஆ"],
   "ளி" ["ள்" "இ"],
   "ளீ" ["ள்" "ஈ"],
   "ளு" ["ள்" "உ"],
   "ளூ" ["ள்" "ஊ"],
   "ளெ" ["ள்" "எ"],
   "ளே" ["ள்" "ஏ"],
   "ளை" ["ள்" "ஐ"],
   "ளொ" ["ள்" "ஒ"],
   "ளோ" ["ள்" "ஓ"],
   "ளௌ" ["ள்" "ஔ"],
   "ற்" ["ற்"],
   "ற" ["ற்" "அ"],
   "றா" ["ற்" "ஆ"],
   "றி" ["ற்" "இ"],
   "றீ" ["ற்" "ஈ"],
   "று" ["ற்" "உ"],
   "றூ" ["ற்" "ஊ"],
   "றெ" ["ற்" "எ"],
   "றே" ["ற்" "ஏ"],
   "றை" ["ற்" "ஐ"],
   "றொ" ["ற்" "ஒ"],
   "றோ" ["ற்" "ஓ"],
   "றௌ" ["ற்" "ஔ"],
   "ன்" ["ன்"],
   "ன" ["ன்" "அ"],
   "னா" ["ன்" "ஆ"],
   "னி" ["ன்" "இ"],
   "னீ" ["ன்" "ஈ"],
   "னு" ["ன்" "உ"],
   "னூ" ["ன்" "ஊ"],
   "னெ" ["ன்" "எ"],
   "னே" ["ன்" "ஏ"],
   "னை" ["ன்" "ஐ"],
   "னொ" ["ன்" "ஒ"],
   "னோ" ["ன்" "ஓ"],
   "னௌ" ["ன்" "ஔ"]})

(def  ^{:doc "a trie of the individual letters in தமிழ், whose terminus-attached values are sequences of each letter's phonemes -- this trie can be used in str->elems for directly splitting a word into its phonemes"}
  phoneme-trie (make-trie phoneme-map))

(def inverse-phoneme-map (set/map-invert phoneme-map))

(defn str->phonemes
  "take a string and split it into its constitutent தமிழ் phonemes"
  [s]
  (str->elems phoneme-trie s))

;; TODO: create a make-inverse-trie fn
;; TODO: turn str->elem into seq->elem, use that to refactor phonemes->str

(defn phonemes->str
  "given a seq of phonemes, create a string where the phonemes are combined into their proper letters"
  [phoneme-seq]
  (let [concat-phoneme-str (apply str phoneme-seq)
        inverse-concat-phoneme-map (into {} (for [[k v] inverse-phoneme-map]
                                              [(apply str k) v]))
        inverse-concat-phoneme-trie (make-trie inverse-concat-phoneme-map)
        combined-phoneme-str (apply str (str->elems inverse-concat-phoneme-trie concat-phoneme-str))]
    combined-phoneme-str))

;;;;;;;;;;;;;;
;; sorting fns
;;;;;;;;;;;;;; 

(def ^{:private false
       :doc "a flattened seq of all தமிழ் letters in lexicographical (alphabetical) order -- put anohter way, in the order of அகர முதல் னரக இறுவாய் as the 2500 yr old grammatical compendium தொல்காப்பியம் states in its outset"}
  letter-seq (flatten (concat vowels c-cv-letters)))

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
  (loop [s1 (str->elems str1)
         s2 (str->elems str2)]
    (cond (not (seq s1)) (boolean (seq s2))
          (not (seq s2)) false 
          (not= (first s1) (first s2)) (letter-before? (first s1) (first s2))
          :else (recur (rest s1) (rest s2)))))

(def ^{:doc "a comparator for lexicographical comparisons of arbitrary strings (consisting of தமிழ் letters and letters from 1-to-1 encodings)"}
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

;; TODO: DRY on seq-prefix & seq-prefix? -- is there a Clojure implementation?

(defn seq-prefix
  "return the shared prefix between the 2 input sequence"
  [seq1 seq2] 
  (loop [s1 seq1
         s2 seq2
         comm-prefix []]
    (let [f1 (first s1)
          f2 (first s2)]
      (if (or (empty? s1)
              (empty? s2)
              (not= f1 f2))
        comm-prefix
        (recur (rest s1) (rest s2) (conj comm-prefix f1))))))

(defn seq-prefix?
  "return whether the query seq is a prefix of the target"
  [tgt qry]
  (let [pfx (seq-prefix tgt qry)]
    (boolean
     (and (seq tgt)
          (or (= (seq qry) pfx)
              (and (empty? qry) (empty? pfx)))))))

(defn prefix?
  "return whether the 2nd word is a prefix of the 1st word, based on தமிழ் phonemes"
  [str1 str2]
  (let [phonemes1 (str->elems phoneme-trie str1)
        phonemes2 (str->elems phoneme-trie str2)]
    (seq-prefix? phonemes1 phonemes2)))

(defn suffix?
  "return whether the 2nd word is a suffix of the 1st word, based on தமிழ் phonemes"
  [str1 str2]
  (let [phonemes1 (str->elems phoneme-trie str1)
        phonemes2 (str->elems phoneme-trie str2)]
    (seq-prefix? (reverse phonemes1) (reverse phonemes2))))

;; TODO: DRY on seq-index-of -- is there already a Clojure implementation?

(defn seq-index-of
  "given a target seq and a query seq, return the 0-based index of the first occurrence of the query seq appearing inside the target seq, or else return -1 (is that Clojure-y, or is returning nil more Clojure-y?)
  calls seq-prefix? at every index -- only realizes the target seq as needed, pulls query seq into memory"
  [tgt qry]
  (let [qlen (count qry)]
    (loop [ts tgt
           idx 0]
      (if (or (empty? ts)
              (< (count (take qlen ts)) qlen))
        -1
        (if (seq-prefix? ts qry)
          idx
          (recur (rest ts) (inc idx)))))))

(def ^{:doc "a wrapper around the native fn call that gives the index of the first occurrence of a particular substring"}
  index-of
  #+cljs seq-index-of 
  #+clj (fn [tgt qry]
            (.indexOf tgt qry)))

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
        prev-chunk-idx (if prev-chunk (index-of before prev-chunk) -1)
        next-chunk-idx (if next-chunk (index-of after next-chunk) -1)
        prev-chunk-flush (= idx (+ prev-chunk-idx (count prev-chunk)))
        next-chunk-flush (zero? next-chunk-idx)]
    (cond
     (and prev-chunk-wordiness next-chunk-wordiness prev-chunk-flush next-chunk-flush) [(str prev-chunk next-chunk) (- idx prev-chunk-idx)]
     (and prev-chunk-wordiness prev-chunk-flush) [prev-chunk (- idx prev-chunk-idx)]
     (and  next-chunk-wordiness next-chunk-flush) [next-chunk 0]
     :else nil)))

(def wordy-chunk-under (comp first wordy-chunk-and-cursor-pos))

(defn cursor-adjust
  "given a string, a cursor position (idx), and a direction, give the new position of the cursor that that is on the boundary of the actual letters"
  [s idx direction]
  (let [[wordy-chunk rel-idx] (wordy-chunk-and-cursor-pos s idx)
        letters (str->letters wordy-chunk)
        indices (reductions #(+ %1 (count %2)) 0 letters)
        before-idx (->> indices
                        (take-while #(<= % idx))
                        last)
        after-idx (->> indices
                       (drop-while #(< % idx))
                       first)]
    (if (= before-idx after-idx)
      (do
        (assert (= idx before-idx after-idx))
        idx)
      (case direction
        (:to-first :முதல்-நோக்கி) before-idx
        (:to-last :பின்-நோக்கி)  after-idx
        after-idx))))
