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

;;;;;;;;
;; தமிழ் <-> TAB
;;;;;;;;

(def tab-letter-seq
  ["Ü"
   "Ý"
   "Þ"
   "ß"
   "à"
   "á"
   "â"
   "ã"
   "ä"
   "å"
   "æ"
   "å÷"
   "ç"
   "è¢"
   "è"
   "è£"
   "è¤"
   "è¦"
   "°"
   "Ã"
   "ªè"
   "«è"
   "¬è"
   "ªè£"
   "«è£"
   "ªè÷"
   "é¢"
   "é"
   "é£"
   "é¤"
   "é¦"
   "±"
   "ô"
   "ªé"
   "«é"
   "¬é"
   "ªé£"
   "«é£"
   "ªé÷"
   "ê¢"
   "ê"
   "ê£"
   "ê¤"
   "ê¦"
   "²"
   "õ"
   "ªê"
   "«ê"
   "¬ê"
   "ªê£"
   "«ê£"
   "ªê÷"
   "ë¢"
   "ë"
   "ë£"
   "ë¤"
   "ë¦"
   "³"
   "ö"
   "ªë"
   "«ë"
   "¬ë"
   "ªë£"
   "«ë£"
   "ªë÷"
   "ì¢"
   "ì"
   "ì£"
   "®"
   "ì¦"
   "´"
   "÷"
   "ªì"
   "«ì"
   "¬ì"
   "ªì£"
   "«ì£"
   "ªì÷"
   "í¢"
   "í"
   "í£"
   "í¤"
   "í¦"
   "µ"
   "È"
   "ªí"
   "«í"
   "¬í"
   "ªí£"
   "«í£"
   "ªí÷"
   "î¢"
   "î"
   "î£"
   "î¤"
   "î¦"
   "¶"
   "ù"
   "ªî"
   "«î"
   "¬î"
   "ªî£"
   "«î£"
   "ªî÷"
   "ï¢"
   "ï"
   "ï£"
   "ï¤"
   "ï¦"
   "¸"
   "Ë"
   "ªï"
   "«ï"
   "¬ï"
   "ªï£"
   "«ï£"
   "ªï÷"
   "ð¢"
   "ð"
   "ð£"
   "ð¤"
   "ð¦"
   "¹"
   "Ì"
   "ªð"
   "«ð"
   "¬ð"
   "ªð£"
   "«ð£"
   "ªð÷"
   "ñ¢"
   "ñ"
   "ñ£"
   "ñ¤"
   "ñ¦"
   "º"
   "Í"
   "ªñ"
   "«ñ"
   "¬ñ"
   "ªñ£"
   "«ñ£"
   "ªñ÷"
   "ò¢"
   "ò"
   "ò£"
   "ò¤"
   "ò¦"
   "»"
   "Î"
   "ªò"
   "«ò"
   "¬ò"
   "ªò£"
   "«ò£"
   "ªò÷"
   "ó¢"
   "ó"
   "ó£"
   "ó¤"
   "ó£"
   "¼"
   "Ï"
   "ªó"
   "«ó"
   "¬ó"
   "ªó£"
   "«ó£"
   "ªó÷"
   "ô¢"
   "ô"
   "ô£"
   "ô¤"
   "ô¦"
   "½"
   "Ö"
   "ªô"
   "«ô"
   "¬ô"
   "ªô£"
   "«ô£"
   "ªô÷"
   "õ¢"
   "õ"
   "õ£"
   "õ¤"
   "õ¦"
   "¾"
   "×"
   "ªõ"
   "«õ"
   "¬õ"
   "ªõ£"
   "«õ£"
   "ªõ÷"
   "ö¢"
   "ö"
   "ö£"
   "ö¤"
   "ö¦"
   "¿"
   "Ø"
   "ªö"
   "¬ö"
   "ªö£"
   "«ö£"
   "ªö÷"
   "÷¢"
   "÷"
   "÷£"
   "÷¤"
   "÷¦"
   "À"
   "Ù"
   "ª÷"
   "«÷"
   "¬÷"
   "ª÷£"
   "«÷£"
   "ª÷÷"
   "ø¢"
   "ø"
   "ø£"
   "ø¤"
   "ø¦"
   "Á"
   "Ú"
   "ªø"
   "«ø"
   "¬ø"
   "ªø£"
   "«ø£"
   "ªø÷"
   "ù¢"
   "ù"
   "ù£"
   "ù¤"
   "ù¦"
   "Â"
   "Û"
   "ªù"
   "«ù"
   "¬ù"
   "ªù£"
   "«ù£"
   "ªù÷"]
  )

(def தமிழ்-tab-map (zipmap fmt/letter-seq tab-letter-seq))

(def tab-தமிழ்-map (set/map-invert தமிழ்-tab-map))

(def தமிழ்-tab-trie (fmt/make-trie தமிழ்-tab-map))

(def tab-தமிழ்-trie (fmt/make-trie tab-தமிழ்-map))

(defn தமிழ்->tab
  "convert தமிழ் text from unicode to TAB format"
  [s]
  (->> (fmt/str->elems தமிழ்-tab-trie s)
       (apply str)))

(defn tab->தமிழ்
  "convert தமிழ் text from TAB to unicode format"
  [s]
  (->> (fmt/str->elems tab-தமிழ்-trie s)
       (apply str)))

;;;;;;;;
;; தமிழ் <-> Bamini
;;;;;;;;

(def bamini-letter-seq
  ["m"
   "M"
   ","
   "<"
   "c"
   "C"
   "v"
   "V"
   "I"
   "x"
   "X"
   "xs"
   "/"
   "f;"
   "f"
   "fh"
   "fp"
   "fP"
   "F"
   "$"
   "nf"
   "Nf"
   "if"
   "nfh"
   "Nfh"
   "nfs"
   "q;"
   "q"
   "qh"
   "qp"
   "qP"
   "*"
   "*"
   "nq"
   "Nq"
   "iq"
   "nqh"
   "Nqh"
   "nqs"
   "r;"
   "r"
   "rh"
   "rp"
   "rP"
   "R"
   "R+"
   "nr"
   "Nr"
   "ir"
   "nrh"
   "Nrh"
   "nrs"
   "Q;"
   "Q"
   "Qh"
   "Qp"
   "QP"
   "*"
   "*"
   "nQ"
   "NQ"
   "iQ"
   "nQh"
   "NQh"
   "nQs"
   "l;"
   "l"
   "lh"
   "b"
   "B"
   "L"
   "^"
   "nl"
   "Nl"
   "il"
   "nlh"
   "Nlh"
   "nls"
   "z;"
   "z"
   "zh"
   "zp"
   "zP"
   "Z"
   "Z}"
   "nz"
   "Nz"
   "iz"
   "nzh"
   "Nzh"
   "nzs"
   "j;"
   "j"
   "jh"
   "jp"
   "jP"
   "J"
   "J}"
   "nj"
   "Nj"
   "ij"
   "njh"
   "Njh"
   "njs"
   "e;"
   "e"
   "eh"
   "ep"
   "eP"
   "E"
   "E}"
   "ne"
   "Ne"
   "ie"
   "neh"
   "Neh"
   "nes"
   "g;"
   "g"
   "gh"
   "gp"
   "gP"
   "G"
   "G+"
   "ng"
   "Ng"
   "ig"
   "ngh"
   "Ngh"
   "ngs"
   "k;"
   "k"
   "kh"
   "kp"
   "kP"
   "K"
   "%"
   "nk"
   "Nk"
   "ik"
   "nkh"
   "Nkh"
   "nks"
   "a;"
   "a"
   "ah"
   "ap"
   "aP"
   "A"
   "A+"
   "na"
   "Na"
   "ia"
   "nah"
   "Nah"
   "nas"
   "u;"
   "u"
   "uh"
   "up"
   "uP"
   "U"
   "&"
   "nu"
   "Nu"
   "iu"
   "nuh"
   "Nuh"
   "nus"
   "y;"
   "y"
   "yh"
   "yp"
   "yP"
   "Y"
   "Y}"
   "ny"
   "Ny"
   "iy"
   "nyh"
   "Nyh"
   "nys"
   "t;"
   "t"
   "th"
   "tp"
   "tP"
   "T"
   "T+"
   "nt"
   "Nt"
   "it"
   "nth"
   "Nth"
   "nts"
   "o;"
   "o"
   "oh"
   "op"
   "oP"
   "O"
   "*"
   "no"
   "io"
   "noh"
   "Noh"
   "nos"
   "s;"
   "s"
   "sh"
   "sp"
   "sP"
   "S"
   "Sh"
   "ns"
   "Ns"
   "is"
   "nsh"
   "Nsh"
   "nss"
   "w;"
   "w"
   "wh"
   "wp"
   "wP"
   "W"
   "W}"
   "nw"
   "Nw"
   "iw"
   "nwh"
   "Nwh"
   "nws"
   "d;"
   "d"
   "dh"
   "dp"
   "dP"
   "D"
   "D}"
   "nd"
   "Nd"
   "id"
   "ndh"
   "Ndh"
   "nds"])

(def தமிழ்-bamini-map (zipmap fmt/letter-seq bamini-letter-seq))

(def bamini-தமிழ்-map (set/map-invert தமிழ்-bamini-map))

(def தமிழ்-bamini-trie (fmt/make-trie தமிழ்-bamini-map))

(def bamini-தமிழ்-trie (fmt/make-trie bamini-தமிழ்-map))

(defn தமிழ்->bamini
  "convert தமிழ் text from unicode to Bamini format"
  [s]
  (->> (fmt/str->elems தமிழ்-bamini-trie s)
       (apply str)))

(defn bamini->தமிழ்
  "convert தமிழ் text from Bamini to unicode format"
  [s]
  (->> (fmt/str->elems bamini-தமிழ்-trie s)
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
