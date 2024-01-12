(ns clj-thamil.format.convert
  (:require ;; [clojure.algo.generic.functor :as ftor]
            [clojure.set :as set] 
            [clj-thamil.format :as fmt])
  #?(:clj (:gen-class)))

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

(def ^{:doc "a map of English strings to their தமிழ் phonemes (and consonant clusters)."}
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
   "ங்க்" "ng"
   "ஆ" "aa"
   "ஈ" "ii"
   "ஊ" "uu"
   "ன்ப்" "nb"
   "ண்ப்" "nb"})

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

(def tab-map
  {"அ" "Ü"
   "ஆ" "Ý"
   "இ" "Þ"
   "ஈ" "ß"
   "உ" "à"
   "ஊ" "á"
   "எ" "â"
   "ஏ" "ã"
   "ஐ" "ä"
   "ஒ" "å"
   "ஓ" "æ"
   "ஔ" "å÷"
   "ஃ" "ç"
   "க்" "è¢"
   "க" "è"
   "கா" "è£"
   "கி" "è¤"
   "கீ" "è¦"
   "கு" "°"
   "கூ" "Ã"
   "கெ" "ªè"
   "கே" "«è"
   "கை" "¬è"
   "கொ" "ªè£"
   "கோ" "«è£"
   "கௌ" "ªè÷"
   "ங்" "é¢"
   "ங" "é"
   "ஙா" "é£"
   "ஙி" "é¤"
   "ஙீ" "é¦"
   "ஙு" "±"
   "ஙூ" "Ä"
   "ஙெ" "ªé"
   "ஙே" "«é"
   "ஙை" "¬é"
   "ஙொ" "ªé£"
   "ஙோ" "«é£"
   "ஙௌ" "ªé÷"
   "ச்" "ê¢"
   "ச" "ê"
   "சா" "ê£"
   "சி" "ê¤"
   "சீ" "ê¦"
   "சு" "²"
   "சூ" "Å"
   "செ" "ªê"
   "சே" "«ê"
   "சை" "¬ê"
   "சொ" "ªê£"
   "சோ" "«ê£"
   "சௌ" "ªê÷"
   "ஞ்" "ë¢"
   "ஞ" "ë"
   "ஞா" "ë£"
   "ஞி" "ë¤"
   "ஞீ" "ë¦"
   "ஞு" "³"
   "ஞூ" "Æ"
   "ஞெ" "ªë"
   "ஞே" "«ë"
   "ஞை" "¬ë"
   "ஞொ" "ªë£"
   "ஞோ" "«ë£"
   "ஞௌ" "ªë÷"
   "ட்" "ì¢"
   "ட" "ì"
   "டா" "ì£"
   "டி" "®"
   "டீ" "ì¦"
   "டு" "´"
   "டூ" "Ç"
   "டெ" "ªì"
   "டே" "«ì"
   "டை" "¬ì"
   "டொ" "ªì£"
   "டோ" "«ì£"
   "டௌ" "ªì÷"
   "ண்" "í¢"
   "ண" "í"
   "ணா" "í£"
   "ணி" "í¤"
   "ணீ" "í¦"
   "ணு" "µ"
   "ணூ" "È"
   "ணெ" "ªí"
   "ணே" "«í"
   "ணை" "¬í"
   "ணொ" "ªí£"
   "ணோ" "«í£"
   "ணௌ" "ªí÷"
   "த்" "î¢"
   "த" "î"
   "தா" "î£"
   "தி" "î¤"
   "தீ" "î¦"
   "து" "¶"
   "தூ" "É"
   "தெ" "ªî"
   "தே" "«î"
   "தை" "¬î"
   "தொ" "ªî£"
   "தோ" "«î£"
   "தௌ" "ªî÷"
   "ந்" "ï¢"
   "ந" "ï"
   "நா" "ï£"
   "நி" "ï¤"
   "நீ" "ï¦"
   "நு" "¸"
   "நூ" "Ë"
   "நெ" "ªï"
   "நே" "«ï"
   "நை" "¬ï"
   "நொ" "ªï£"
   "நோ" "«ï£"
   "நௌ" "ªï÷"
   "ப்" "ð¢"
   "ப" "ð"
   "பா" "ð£"
   "பி" "ð¤"
   "பீ" "ð¦"
   "பு" "¹"
   "பூ" "Ì"
   "பெ" "ªð"
   "பே" "«ð"
   "பை" "¬ð"
   "பொ" "ªð£"
   "போ" "«ð£"
   "பௌ" "ªð÷"
   "ம்" "ñ¢"
   "ம" "ñ"
   "மா" "ñ£"
   "மி" "ñ¤"
   "மீ" "ñ¦"
   "மு" "º"
   "மூ" "Í"
   "மெ" "ªñ"
   "மே" "«ñ"
   "மை" "¬ñ"
   "மொ" "ªñ£"
   "மோ" "«ñ£"
   "மௌ" "ªñ÷"
   "ய்" "ò¢"
   "ய" "ò"
   "யா" "ò£"
   "யி" "ò¤"
   "யீ" "ò¦"
   "யு" "»"
   "யூ" "Î"
   "யெ" "ªò"
   "யே" "«ò"
   "யை" "¬ò"
   "யொ" "ªò£"
   "யோ" "«ò£"
   "யௌ" "ªò÷"
   "ர்" "ó¢"
   "ர" "ó"
   "ரா" "ó£"
   "ரி" "ó¤"
   "ரீ" "ó¦"
   "ரு" "¼"
   "ரூ" "Ï"
   "ரெ" "ªó"
   "ரே" "«ó"
   "ரை" "¬ó"
   "ரொ" "ªó£"
   "ரோ" "«ó£"
   "ரௌ" "ªó÷"
   "ல்" "ô¢"
   "ல" "ô"
   "லா" "ô£"
   "லி" "ô¤"
   "லீ" "ô¦"
   "லு" "½"
   "லூ" "Ö"
   "லெ" "ªô"
   "லே" "«ô"
   "லை" "¬ô"
   "லொ" "ªô£"
   "லோ" "«ô£"
   "லௌ" "ªô÷"
   "வ்" "õ¢"
   "வ" "õ"
   "வா" "õ£"
   "வி" "õ¤"
   "வீ" "õ¦"
   "வு" "¾"
   "வூ" "×"
   "வெ" "ªõ"
   "வே" "«õ"
   "வை" "¬õ"
   "வொ" "ªõ£"
   "வோ" "«õ£"
   "வௌ" "ªõ÷"
   "ழ்" "ö¢"
   "ழ" "ö"
   "ழா" "ö£"
   "ழி" "ö¤"
   "ழீ" "ö¦"
   "ழு" "¿"
   "ழூ" "Ø"
   "ழெ" "ªö"
   "ழே" "«ö"
   "ழை" "¬ö"
   "ழொ" "ªö£"
   "ழோ" "«ö£"
   "ழௌ" "ªö÷"
   "ள்" "÷¢"
   "ள" "÷"
   "ளா" "÷£"
   "ளி" "÷¤"
   "ளீ" "÷¦"
   "ளு" "À"
   "ளூ" "Ù"
   "ளெ" "ª÷"
   "ளே" "«÷"
   "ளை" "¬÷"
   "ளொ" "ª÷£"
   "ளோ" "«÷£"
   "ளௌ" "ª÷÷"
   "ற்" "ø¢"
   "ற" "ø"
   "றா" "ø£"
   "றி" "ø¤"
   "றீ" "ø¦"
   "று" "Á"
   "றூ" "Ú"
   "றெ" "ªø"
   "றே" "«ø"
   "றை" "¬ø"
   "றொ" "ªø£"
   "றோ" "«ø£"
   "றௌ" "ªø÷"
   "ன்" "ù¢"
   "ன" "ù"
   "னா" "ù£"
   "னி" "ù¤"
   "னீ" "ù¦"
   "னு" "Â"
   "னூ" "Û"
   "னெ" "ªù"
   "னே" "«ù"
   "னை" "¬ù"
   "னொ" "ªù£"
   "னோ" "«ù£"
   "னௌ" "ªù÷"})

;;;;;;;;
;; தமிழ் <-> Bamini
;;;;;;;;

(def bamini-map
  {"அ" "m"
   "ஆ" "M"
   "இ" ","
   "ஈ" "<"
   "உ" "c"
   "ஊ" "C"
   "எ" "v"
   "ஏ" "V"
   "ஐ" "I"
   "ஒ" "x"
   "ஓ" "X"
   "ஔ" "xs"
   "ஃ" "/"
   "க்" "f;"
   "க" "f"
   "கா" "fh"
   "கி" "fp"
   "கீ" "fP"
   "கு" "F"
   "கூ" "$"
   "கெ" "nf"
   "கே" "Nf"
   "கை" "if"
   "கொ" "nfh"
   "கோ" "Nfh"
   "கௌ" "nfs"
   "ங்" "q;"
   "ங" "q"
   "ஙா" "qh"
   "ஙி" "qp"
   "ஙீ" "qP"
   ;; "ஙு" nil
   ;; "ஙூ" nil
   "ஙெ" "nq"
   "ஙே" "Nq"
   "ஙை" "iq"
   "ஙொ" "nqh"
   "ஙோ" "Nqh"
   "ஙௌ" "nqs"
   "ச்" "r;"
   "ச" "r"
   "சா" "rh"
   "சி" "rp"
   "சீ" "rP"
   "சு" "R"
   "சூ" "#"
   "செ" "nr"
   "சே" "Nr"
   "சை" "ir"
   "சொ" "nrh"
   "சோ" "Nrh"
   "சௌ" "nrs"
   "ஞ்" "Q;"
   "ஞ" "Q"
   "ஞா" "Qh"
   "ஞி" "Qp"
   "ஞீ" "QP"
   ;; "ஞு" nil
   ;; "ஞூ" nil
   "ஞெ" "nQ"
   "ஞே" "NQ"
   "ஞை" "iQ"
   "ஞொ" "nQh"
   "ஞோ" "NQh"
   "ஞௌ" "nQs"
   "ட்" "l;"
   "ட" "l"
   "டா" "lh"
   "டி" "b"
   "டீ" "lP"
   "டு" "L"
   "டூ" "^"
   "டெ" "nl"
   "டே" "Nl"
   "டை" "il"
   "டொ" "nlh"
   "டோ" "Nlh"
   "டௌ" "nls"
   "ண்" "z;"
   "ண" "z"
   "ணா" "zh"
   "ணி" "zp"
   "ணீ" "zP"
   "ணு" "Z"
   "ணூ" "Z}"
   "ணெ" "nz"
   "ணே" "Nz"
   "ணை" "iz"
   "ணொ" "nzh"
   "ணோ" "Nzh"
   "ணௌ" "nzs"
   "த்" "j;"
   "த" "j"
   "தா" "jh"
   "தி" "jp"
   "தீ" "jP"
   "து" "J"
   "தூ" "J}"
   "தெ" "nj"
   "தே" "Nj"
   "தை" "ij"
   "தொ" "njh"
   "தோ" "Njh"
   "தௌ" "njs"
   "ந்" "e;"
   "ந" "e"
   "நா" "eh"
   "நி" "ep"
   "நீ" "eP"
   "நு" "E"
   "நூ" "E}"
   "நெ" "ne"
   "நே" "Ne"
   "நை" "ie"
   "நொ" "neh"
   "நோ" "Neh"
   "நௌ" "nes"
   "ப்" "g;"
   "ப" "g"
   "பா" "gh"
   "பி" "gp"
   "பீ" "gP"
   "பு" "G"
   "பூ" "G+"
   "பெ" "ng"
   "பே" "Ng"
   "பை" "ig"
   "பொ" "ngh"
   "போ" "Ngh"
   "பௌ" "ngs"
   "ம்" "k;"
   "ம" "k"
   "மா" "kh"
   "மி" "kp"
   "மீ" "kP"
   "மு" "K"
   "மூ" "%"
   "மெ" "nk"
   "மே" "Nk"
   "மை" "ik"
   "மொ" "nkh"
   "மோ" "Nkh"
   "மௌ" "nks"
   "ய்" "a;"
   "ய" "a"
   "யா" "ah"
   "யி" "ap"
   "யீ" "aP"
   "யு" "A"
   "யூ" "A+"
   "யெ" "na"
   "யே" "Na"
   "யை" "ia"
   "யொ" "nah"
   "யோ" "Nah"
   "யௌ" "nas"
   "ர்" "u;"
   "ர" "u"
   "ரா" "uh"
   "ரி" "up"
   "ரீ" "uP"
   "ரு" "U"
   "ரூ" "&"
   "ரெ" "nu"
   "ரே" "Nu"
   "ரை" "iu"
   "ரொ" "nuh"
   "ரோ" "Nuh"
   "ரௌ" "nus"
   "ல்" "y;"
   "ல" "y"
   "லா" "yh"
   "லி" "yp"
   "லீ" "yP"
   "லு" "Y"
   "லூ" "Y}"
   "லெ" "ny"
   "லே" "Ny"
   "லை" "iy"
   "லொ" "nyh"
   "லோ" "Nyh"
   "லௌ" "nys"
   "வ்" "t;"
   "வ" "t"
   "வா" "th"
   "வி" "tp"
   "வீ" "tP"
   "வு" "T"
   "வூ" "T+"
   "வெ" "nt"
   "வே" "Nt"
   "வை" "it"
   "வொ" "nth"
   "வோ" "Nth"
   "வௌ" "ntt"
   "ழ்" "o;"
   "ழ" "o"
   "ழா" "oh"
   "ழி" "op"
   "ழீ" "oP"
   "ழு" "O"
   "ழூ" "*"
   "ழெ" "no"
   "ழே" "No"
   "ழை" "io"
   "ழொ" "noh"
   "ழோ" "Noh"
   "ழௌ" "noo"
   "ள்" "s;"
   "ள" "s"
   "ளா" "sh"
   "ளி" "sp"
   "ளீ" "sP"
   "ளு" "S"
   "ளூ" "Sh"
   "ளெ" "ns"
   "ளே" "Ns"
   "ளை" "is"
   "ளொ" "nsh"
   "ளோ" "Nsh"
   "ளௌ" "nss"
   "ற்" "w;"
   "ற" "w"
   "றா" "wh"
   "றி" "wp"
   "றீ" "wP"
   "று" "W"
   "றூ" "W}"
   "றெ" "nw"
   "றே" "Nw"
   "றை" "iw"
   "றொ" "nwh"
   "றோ" "Nwh"
   "றௌ" "nws"
   "ன்" "d;"
   "ன" "d"
   "னா" "dh"
   "னி" "dp"
   "னீ" "dP"
   "னு" "D"
   "னூ" "D}"
   "னெ" "nd"
   "னே" "Nd"
   "னை" "id"
   "னொ" "ndh"
   "னோ" "Ndh"
   "னௌ" "nds"

   "ஜ்" "[;"
   "ஜ" "["
   "ஜா" "[h"
   "ஜி" "[p"
   "ஜீ" "[P"
   "ஜு" "[{"
   "ஜூ" "[\""
   "ஜெ" "n["
   "ஜே" "N["
   "ஜை" "i["
   "ஜொ" "n[h"
   "ஜோ" "N[h"
   "ஜௌ" "n[s"

   "ஷ்" "\\;"
   "ஷ" "\\"
   "ஷா" "\\h"
   "ஷி" "\\p"
   "ஷீ" "\\P"
   "ஷு" "\\{"
   "ஷூ" "\\\""
   "ஷெ" "n\\"
   "ஷே" "N\\"
   "ஷை" "i\\"
   "ஷொ" "n\\h"
   "ஷோ" "N\\h"
   "ஷௌ" "n\\s"

   "ஸ்" "];"
   "ஸ" "]"
   "ஸா" "]h"
   "ஸி" "]p"
   "ஸீ" "]P"
   "ஸு" "]{"
   "ஸூ" "]\""
   "ஸெ" "n]"
   "ஸே" "N]"
   "ஸை" "i]"
   "ஸொ" "n]h"
   "ஸோ" "N]h"
   "ஸௌ" "n]s"

   "ஹ்" "`;"
   "ஹ" "`"
   "ஹா" "`h"
   "ஹி" "`p"
   "ஹீ" "`P"
   "ஹு" "`{"
   "ஹூ" "`\""
   "ஹெ" "n`"
   "ஹே" "N`"
   "ஹை" "i`"
   "ஹொ" "n`h"
   "ஹோ" "N`h"
   "ஹௌ" "n`s"

   "க்ஷ்" "~;"

   "ஶ்ரீ" "="
   
   }) 

;;;;;;;;
;; தமிழ் <-> TSCII
;;;;;;;;

(def tscii-map
  {"அ" "«"
   "ஆ" "¬"
   "இ" "­"
   "ஈ" "®"
   "உ" "¯"
   "ஊ" "°"
   "எ" "±"
   "ஏ" "²"
   "ஐ" "³"
   "ஒ" "´"
   "ஓ" "µ"
   "ஔ" "¶"
   "ஃ" "∙"
   "க்" "ì"
   "க" "¸"
   "கா" "¸¡"
   "கி" "¸¢"
   "கீ" "¸£"
   "கு" "Ì"
   "கூ" "Ü"
   "கெ" "¦¸"
   "கே" "§¸"
   "கை" "¨¸"
   "கொ" "¦¸¡"
   "கோ" "§¸¡"
   "கௌ" "¦¸ª"
   "ங்" "í"
   "ங" "¹"
   "ஙா" "¹¡"
   "ஙி" "¹¢"
   "ஙீ" "¹£"
   "ஙு" "™"
   "ஙூ" "›"
   "ஙெ" "¦¹"
   "ஙே" "§¹"
   "ஙை" "¨¹"
   "ஙொ" "¦¹¡"
   "ஙோ" "§¹¡"
   "ஙௌ" "¦¹ª"
   "ச்" "î"
   "ச" "º"
   "சா" "º¡"
   "சி" "º¢"
   "சீ" "º£"
   "சு" "Í"
   "சூ" "Ý"
   "செ" "¦º"
   "சே" "§º"
   "சை" "¨º"
   "சொ" "¦º¡"
   "சோ" "§º¡"
   "சௌ" "¦ºª"
   "ஞ்" "ï"
   "ஞ" "»"
   "ஞா" "»¡"
   "ஞி" "»¢"
   "ஞீ" "»£"
   "ஞு" ""
   "ஞூ" "œ"
   "ஞெ" "¦»"
   "ஞே" "§»"
   "ஞை" "¨»"
   "ஞொ" "¦»¡"
   "ஞோ" "§»¡"
   "ஞௌ" "¦»ª"
   "ட்" "ð"
   "ட" "¼"
   "டா" "¼¡"
   "டி" "Ê"
   "டீ" "Ë"
   "டு" "Î"
   "டூ" "Þ"
   "டெ" "¦¼"
   "டே" "§¼"
   "டை" "¨¼"
   "டொ" "¦¼¡"
   "டோ" "§¼¡"
   "டௌ" "¦¼ª"
   "ண்" "ñ"
   "ண" "½"
   "ணா" "½¡"
   "ணி" "½¢"
   "ணீ" "½£"
   "ணு" "Ï"
   "ணூ" "ß"
   "ணெ" "¦½"
   "ணே" "§½"
   "ணை" "¨½"
   "ணொ" "¦½¡"
   "ணோ" "§½¡"
   "ணௌ" "¦½ª"
   "த்" "ò"
   "த" "¾"
   "தா" "¾¡"
   "தி" "¾¢"
   "தீ" "¾£"
   "து" "Ð"
   "தூ" "à"
   "தெ" "¦¾"
   "தே" "§¾"
   "தை" "¨¾"
   "தொ" "¦¾¡"
   "தோ" "§¾¡"
   "தௌ" "¦¾ª"
   "ந்" "ó"
   "ந" "¿"
   "நா" "¿¡"
   "நி" "¿¢"
   "நீ" "¿£"
   "நு" "Ñ"
   "நூ" "á"
   "நெ" "¦¿"
   "நே" "§¿"
   "நை" "¨¿"
   "நொ" "¦¿¡"
   "நோ" "§¿¡"
   "நௌ" "¦¿ª"
   "ப்" "ô"
   "ப" "À"
   "பா" "À¡"
   "பி" "À¢"
   "பீ" "À£"
   "பு" "Ò"
   "பூ" "â"
   "பெ" "¦À"
   "பே" "§À"
   "பை" "¨À"
   "பொ" "¦À¡"
   "போ" "§À¡"
   "பௌ" "¦Àª"
   "ம்" "õ"
   "ம" "Á"
   "மா" "Á¡"
   "மி" "Á¢"
   "மீ" "Á£"
   "மு" "Ó"
   "மூ" "ã"
   "மெ" "¦Á"
   "மே" "§Á"
   "மை" "¨Á"
   "மொ" "¦Á¡"
   "மோ" "§Á¡"
   "மௌ" "¦Áª"
   "ய்" "ö"
   "ய" "Â"
   "யா" "Â¡"
   "யி" "Â¢"
   "யீ" "Â£"
   "யு" "Ô"
   "யூ" "ä"
   "யெ" "¦Â"
   "யே" "§Â"
   "யை" "¨Â"
   "யொ" "¦Â¡"
   "யோ" "§Â¡"
   "யௌ" "¦Âª"
   "ர்" "÷"
   "ர" "Ã"
   "ரா" "Ã¡"
   "ரி" "Ã¢"
   "ரீ" "Ã£"
   "ரு" "Õ"
   "ரூ" "å"
   "ரெ" "¦Ã"
   "ரே" "§Ã"
   "ரை" "¨Ã"
   "ரொ" "¦Ã¡"
   "ரோ" "§Ã¡"
   "ரௌ" "¦Ãª"
   "ல்" "ø"
   "ல" "Ä"
   "லா" "Ä¡"
   "லி" "Ä¢"
   "லீ" "Ä£"
   "லு" "Ö"
   "லூ" "æ"
   "லெ" "¦Ä"
   "லே" "§Ä"
   "லை" "¨Ä"
   "லொ" "¦Ä¡"
   "லோ" "§Ä¡"
   "லௌ" "¦Äª"
   "வ்" "ù"
   "வ" "Å"
   "வா" "Å¡"
   "வி" "Å¢"
   "வீ" "Å£"
   "வு" "×"
   "வூ" "ç"
   "வெ" "¦Å"
   "வே" "§Å"
   "வை" "¨Å"
   "வொ" "¦Å¡"
   "வோ" "§Å¡"
   "வௌ" "¦Åª"
   "ழ்" "ú"
   "ழ" "Æ"
   "ழா" "Æ¡"
   "ழி" "Æ¢"
   "ழீ" "Æ£"
   "ழு" "Ø"
   "ழூ" "è"
   "ழெ" "¦Æ"
   "ழே" "§Æ"
   "ழை" "¨Æ"
   "ழொ" "¦Æ¡"
   "ழோ" "§Æ¡"
   "ழௌ" "¦Æª"
   "ள்" "û"
   "ள" "Ç"
   "ளா" "Ç¡"
   "ளி" "Ç¢"
   "ளீ" "Ç£"
   "ளு" "Ù"
   "ளூ" "é"
   "ளெ" "¦Ç"
   "ளே" "§Ç"
   "ளை" "¨Ç"
   "ளொ" "¦Ç¡"
   "ளோ" "§Ç¡"
   "ளௌ" "¦Çª"
   "ற்" "ü"
   "ற" "È"
   "றா" "È¡"
   "றி" "È¢"
   "றீ" "È£"
   "று" "Ú"
   "றூ" "ê"
   "றெ" "¦È"
   "றே" "§È"
   "றை" "¨È"
   "றொ" "¦È¡"
   "றோ" "§È¡"
   "றௌ" "¦Èª"
   "ன்" "ý"
   "ன" "É"
   "னா" "É¡"
   "னி" "É¢"
   "னீ" "É£"
   "னு" "Û"
   "னூ" "ë"
   "னெ" "¦É"
   "னே" "§É"
   "னை" "¨É"
   "னொ" "¦É¡"
   "னோ" "§É¡"
   "னௌ" "¦Éª"})

;;;;;;;;
;; தமிழ் <-> Webulagam
;;;;;;;;

(def webulagam-map
  {"அ" "m"
   "ஆ" "M"
   "இ" "ï"
   "ஈ" "<"
   "உ" "c"
   "ஊ" "C"
   "எ" "v"
   "ஏ" "V"
   "ஐ" "I"
   "ஒ" "x"
   "ஓ" "X"
   "ஔ" "xs"
   "ஃ" "~"
   "க்" "¡"
   "க" "f"
   "கா" "fh"
   "கி" "»"
   "கீ" "Ñ"
   "கு" "F"
   "கூ" "T"
   "கெ" "bf"
   "கே" "nf"
   "கை" "if"
   "கொ" "bfh"
   "கோ" "nfh"
   "கௌ" "bfs"
   "ங்" "§"
   "ங" "‡"
   "ஙா" "‡h"
   "ஙி" "À"
   "ஙீ" "†"
   "ஙு" "¼"
   "ஙூ" "½"
   "ஙெ" "b‡"
   "ஙே" "n‡"
   "ஙை" "i‡"
   "ஙொ" "b‡h"
   "ஙோ" "n‡h"
   "ஙௌ" "b‡s"
   "ச்" "¢"
   "ச" "r"
   "சா" "rh"
   "சி" "á"
   "சீ" "Ó"
   "சு" "R"
   "சூ" "N"
   "செ" "br"
   "சே" "nr"
   "சை" "ir"
   "சொ" "brh"
   "சோ" "nrh"
   "சௌ" "brs"
   "ஞ்" "Š"
   "ஞ" "P"
   "ஞா" "Ph"
   "ஞி" "Á"
   "ஞீ" "Ø"
   "ஞு" "|"
   "ஞூ" "ú"
   "ஞெ" "bP"
   "ஞே" "nP"
   "ஞை" "iP"
   "ஞொ" "bPh"
   "ஞோ" "nPh"
   "ஞௌ" "bPs"
   "ட்" "£"
   "ட" "l"
   "டா" "lh"
   "டி" "o"
   "டீ" "O"
   "டு" "L"
   "டூ" "^"
   "டெ" "bl"
   "டே" "nl"
   "டை" "il"
   "டொ" "blh"
   "டோ" "nlh"
   "டௌ" "bls"
   "ண்" "©"
   "ண" "z"
   "ணா" "zh"
   "ணி" "Â"
   "ணீ" "Ù"
   "ணு" "Q"
   "ணூ" "û"
   "ணெ" "bz"
   "ணே" "nz"
   "ணை" "iz"
   "ணொ" "bzh"
   "ணோ" "nzh"
   "ணௌ" "bzs"
   "த்" "¤"
   "த" "j"
   "தா" "jh"
   "தி" "â"
   "தீ" "Ô"
   "து" "J"
   "தூ" "ö"
   "தெ" "bj"
   "தே" "nj"
   "தை" "ij"
   "தொ" "bjh"
   "தோ" "njh"
   "தௌ" "bjs"
   "ந்" "ª"
   "ந" "e"
   "நா" "eh"
   "நி" "Ã"
   "நீ" "Ú"
   "நு" "E"
   "நூ" "ü"
   "நெ" "be"
   "நே" "ne"
   "நை" "ie"
   "நொ" "beh"
   "நோ" "neh"
   "நௌ" "bes"
   "ப்" "¥"
   "ப" "g"
   "பா" "gh"
   "பி" "ã"
   "பீ" "Õ"
   "பு" "ò"
   "பூ" "ó"
   "பெ" "bg"
   "பே" "ng"
   "பை" "ig"
   "பொ" "bgh"
   "போ" "ngh"
   "பௌ" "bgs"
   "ம்" "«"
   "ம" "k"
   "மா" "kh"
   "மி" "Ä"
   "மீ" "Û"
   "மு" "K"
   "மூ" "_"
   "மெ" "bk"
   "மே" "nk"
   "மை" "ik"
   "மொ" "bkh"
   "மோ" "nkh"
   "மௌ" "bks"
   "ய்" "Œ"
   "ய" "a"
   "யா" "ah"
   "யி" "Æ"
   "யீ" "p"
   "யு" "í"
   "யூ" "ô"
   "யெ" "ba"
   "யே" "na"
   "யை" "ia"
   "யொ" "bah"
   "யோ" "nah"
   "யௌ" "bas"
   "ர்" "®"
   "ர" "u"
   "ரா" "uh"
   "ரி" "Ç"
   "ரீ" "ß"
   "ரு" "U"
   "ரூ" "%"
   "ரெ" "bu"
   "ரே" "nu"
   "ரை" "iu"
   "ரொ" "buh"
   "ரோ" "nuh"
   "ரௌ" "bus"
   "ல்" "š"
   "ல" "y"
   "லா" "yh"
   "லி" "È"
   "லீ" "ä"
   "லு" "Y"
   "லூ" "ÿ"
   "லெ" "by"
   "லே" "ny"
   "லை" "iy"
   "லொ" "byh"
   "லோ" "nyh"
   "லௌ" "bys"
   "வ்" "›"
   "வ" "t"
   "வா" "th"
   "வி" "É"
   "வீ" "å"
   "வு" "î"
   "வூ" "ñ"
   "வெ" "bt"
   "வே" "nt"
   "வை" "it"
   "வொ" "bth"
   "வோ" "nth"
   "வௌ" "bts"
   "ழ்" "œ"
   "ழ" "H"
   "ழா" "Hh"
   "ழி" "Ê"
   "ழீ" "æ"
   "ழு" "G"
   "ழூ" ">"
   "ழெ" "bH"
   "ழே" "nH"
   "ழை" "iH"
   "ழொ" "bHh"
   "ழோ" "nHh"
   "ழௌ" "bHs"
   "ள்" "Ÿ"
   "ள" "s"
   "ளா" "sh"
   "ளி" "Ë"
   "ளீ" "ç"
   "ளு" "S"
   "ளூ" "q"
   "ளெ" "bs"
   "ளே" "ns"
   "ளை" "is"
   "ளொ" "bsh"
   "ளோ" "nsh"
   "ளௌ" "bss"
   "ற்" "‰"
   "ற" "w"
   "றா" "wh"
   "றி" "¿"
   "றீ" "Ö"
   "று" "W"
   "றூ" "ù"
   "றெ" "bw"
   "றே" "nw"
   "றை" "iw"
   "றொ" "bwh"
   "றோ" "nwh"
   "றௌ" "bws"
   "ன்" "‹"
   "ன" "d"
   "னா" "dh"
   "னி" "Å"
   "னீ" "Ü"
   "னு" "D"
   "னூ" "}"
   "னெ" "bd"
   "னே" "nd"
   "னை" "id"
   "னொ" "bdh"
   "னோ" "ndh"
   "னௌ" "bds"})


;;;;;;;;
;; all character sets togeter
;;;;;;;;

(defn fill-in-bamini-to-unic-map
  "Add in the entries in the bamini -> unicode conversion map
  that represents the normal way that ர் ரி ரீ get written by hand"
  [to-unic-map]
  (let [;; c-with-அ-letters (map second fmt/c-cv-letters)
        letters fmt/letters
        entries (for [letter (flatten letters)
                      r-letter ["ர்" "ரி" "ரீ"]]
                  (let [new-val (str letter r-letter)
                        new-key (str (get bamini-map letter)
                                     (get {"ர்" "h;"
                                           "ரி" "hp"
                                           "ரீ" "hP"} r-letter))]
                    [new-key new-val]))
        extra-entries-map (into {} entries)]
    (merge to-unic-map extra-entries-map)))

(defn fill-charset-map
  [{:keys [from-unic-map to-unic-map] :as m}]
  (let [from-unic-trie (fmt/make-trie from-unic-map)
        to-unic-trie (fmt/make-trie to-unic-map)
        from-unic (fn [s]
                  (->> (fmt/str->elems from-unic-trie s)
                       (apply str)))
        to-unic (fn [s]
                    (->> (fmt/str->elems to-unic-trie s)
                         (apply str)))]
    {:to-unicode to-unic
     :from-unicode from-unic}))

(def init-charsets {:tab {:from-unic-map tab-map
                          :to-unic-map (set/map-invert tab-map)}
                    :bamini {:from-unic-map bamini-map
                             :to-unic-map (-> (set/map-invert bamini-map)
                                              fill-in-bamini-to-unic-map
                                              (assoc ">" ",")
                                              (assoc "xsp" "ஒளி"))}
                    :tscii {:from-unic-map tscii-map
                            :to-unic-map (set/map-invert tscii-map)}
                    :webulagam {:from-unic-map webulagam-map
                                :to-unic-map (set/map-invert webulagam-map)}})

(defn mmap-vals
  "given a map and a fn, map the fn over the maps vals keeping keys same"
  [f m]
  (letfn [(reduce-fn [curr-map kv]
            (assoc curr-map (first kv) (f (second kv))))]
    (reduce reduce-fn {} m)))

(def charsets (-> (mmap-vals fill-charset-map init-charsets)
                  ;; (ftor/fmap fill-charset-map init-charsets)
                  ;;(reduce-kv #(%1 %2 (fill-charset-map %3)) {} init-charsets)
                  (assoc :romanized {:to-unic romanized->தமிழ்
                                     :from-unic தமிழ்->romanized})))

;;;;;;;;
;; named fns for convert fns
;;;;;;;;

;; TAB

(def ^{:doc "convert தமிழ் text from unicode to TAB format"}
  தமிழ்->tab (get-in charsets [:tab :from-unicode]))

(def ^{:doc "convert தமிழ் text from TAB to unicode format"}
  tab->தமிழ் (get-in charsets [:tab :to-unicode]))

;; Bamini

(def ^{:doc "convert தமிழ் text from unicode to Bamini format"}
  தமிழ்->bamini (get-in charsets [:bamini :from-unicode]))

(def ^{:doc "convert தமிழ் text from Bamini to unicode format"}
  bamini->தமிழ் (get-in charsets [:bamini :to-unicode]))

;; TSCII

(def ^{:doc "convert தமிழ் text from unicode to TSCII format"}
  தமிழ்->tscii (get-in charsets [:tscii :from-unicode]))

(def ^{:doc "convert தமிழ் text from TSCII to unicode format"}
  tscii->தமிழ் (get-in charsets [:tscii :to-unicode]))

;; Webulagam

(def ^{:doc "convert தமிழ் text from unicode to Webulagam format"}
  தமிழ்->webulagam (get-in charsets [:webulagam :from-unicode]))

(def ^{:doc "convert தமிழ் text from Webulagam to unicode format"}
  webulagam->தமிழ் (get-in charsets [:webulagam :to-unicode]))

;;;;;;;;
;; main
;;;;;;;;

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
