(ns clj-thamil.format.convert
  (:require [clojure.set :as set]
            [clj-thamil.format :as fmt])
  (:gen-class))

;; A general note about the conversion and transliteration schemes
;; defined by the maps in this namespace:
;;
;; There may be multiple English letter sequences mapping to the same
;; தமிழ் letter.  Also note that we get the mapping for the reverse conversion
;; by inverting the map (keys become values, and values become keys).
;; When multiple keys map to the same value, and you invert the map,
;; the inverse will have the old value pointing to a single old key
;; which is determined non-deterministically.  Therefore, the inverse
;; map may need to be "manually adjusted" in that case to select a
;; default mapping in the inverse map.


;;;;;;;;
;; தமிழ் <-> Romanized
;;;;;;;;

(def ^{:doc "a map of English strings to theier தமிழ் phonemes (and consonant clusters)."}
  romanized-தமிழ்-phoneme-map
  {"g" "க்"
   "s" "ச்"
   "d" "ட்"
   "w" "ந்"
   "b" "ப்"
   "z" "ழ்"
   "mb" "ம்ப்"
   "nth" "ந்த்"
   "nr" "ன்ற்"
   "nd" "ண்ட்"

   "a" "அ"
   "aa" "ஆ"
   "A" "ஆ"
   "i" "இ"
   "ii" "ஈ"
   "I" "ஈ"
   "u" "உ"
   "uu" "ஊ"
   "U" "ஊ"
   "e" "எ"
   "ee" "ஏ"
   "E" "ஏ"
   "ai" "ஐ"
   "o" "ஒ"
   "oo" "ஓ"
   "O" "ஓ"
   "au" "ஔ"
   "q" "ஃ"
   "k" "க்"
   "ng" "ங்"
   "ch" "ச்"
   "nj" "ஞ்"
   "t" "ட்"
   "N" "ண்"
   "th" "த்"
   "n-" "ந்"
   "p" "ப்"
   "m" "ம்"
   "y" "ய்"
   "r" "ர்"
   "l" "ல்"
   "v" "வ்"
   "zh" "ழ்"
   "L" "ள்"
   "R" "ற்"
   "n" "ன்"})

(def ^{:doc "designates specific transliterations of phonemes / phoneme clusters in the தமிழ்->English direction (ex: resolving situations where multiple English sequences map to a single தமிழ் phoneme)"}
  தமிழ்-romanized-phoneme-overrides
  {"ஓ" "O" 
   "ஏ" "E"
   "க்" "k"
   "ச்" "ch"
   "ட்" "t"
   "ந்" "n"
   "ப்" "p"
   "ழ்" "zh"
   "ங்க்" "ng"})

(def ^{:doc "an inverse of romanized-தமிழ்-phoneme-map, but with a few manual mappings for certain தமிழ் letters that can be input in multiple ways (or whose transliteration into English should be different then how it is input via English)"}
  தமிழ்-romanized-phoneme-map
  (merge (set/map-invert romanized-தமிழ்-phoneme-map)
         தமிழ்-romanized-phoneme-overrides))

(def romanized-தமிழ்-phoneme-trie (fmt/make-trie romanized-தமிழ்-phoneme-map))

(def தமிழ்-romanized-phoneme-trie (fmt/make-trie தமிழ்-romanized-phoneme-map))

(defn romanized->தமிழ்
  "transliterates a string of English (transliterated தமிழ்) into the தமிழ் that it represents"
  [s]
  (fmt/phonemes->str (fmt/str->elems romanized-தமிழ்-phoneme-trie s)))

(defn தமிழ்->romanized
  "transliterates a தமிழ் string into English (transliterated தமிழ்)"
  [s]
  (->> (fmt/str->phonemes s)
       (apply str)
       (fmt/str->elems தமிழ்-romanized-phoneme-trie)
       (apply str)))

(def ^{:doc "version of the Mac OS X input method (keyboard) plugin"}
  OSX-INPUT-METHOD-VER "1.0")

(defn -main
  "generates the output necessary for a Mac OS X 10.x input method (keyboard) plugin"
  [& args]
  (let [vowels (remove #(= % "ஃ") fmt/vowels)
        phon-kv-parts-by-vowel (group-by
                                #(boolean (some #{(second %)} vowels))
                                romanized-தமிழ்-phoneme-map)
        ஃ-map {"q" "ஃ"}
        vowel-map (into {} (get phon-kv-parts-by-vowel true))
        cons-map (into {} (get phon-kv-parts-by-vowel false))
        cv-map (into {} (for [[eng-c tha-c] cons-map
                              [eng-v tha-v] vowel-map]
                          [(str eng-c eng-v) (fmt/phonemes->str [tha-c tha-v])]))
        letters-map (merge ஃ-map vowel-map cons-map cv-map)
        letters-lines (map #(str (first %) " " (second %)) letters-map)
        input-chars-str (->> letters-map
                             keys
                             (map seq)
                             (apply concat)
                             distinct
                             (apply str))
        max-input-code (->> letters-map
                            keys
                            (map count)
                            (apply max))
        lines1 ["METHOD: TABLE"
                "ENCODE: Unicode"
                "PROMPT: கலை"
                "DELIMITER ,"
                (str "VERSION " OSX-INPUT-METHOD-VER)
                (str "MAXINPUTCODE " max-input-code)
                (str "VALIDINPUTKEY " input-chars-str)
                "BEGINCHARACTER"
                ""]
        lines2 [""
                "ENDCHARACTER"]
        all-lines (concat lines1 letters-lines lines2)]
    (dorun (map println all-lines))

    ;; (println "hello")
    ))
