(ns clj-thamil.format)

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

(defn trie-update-subtree
  "given the subtree of a trie as a nested map, and the (sub-)sequence to be inserted in the subtree, return the updated subtree"
  [tm s]
  (loop [idx 0]
    (let [[pre post] (split-at idx s)]
      (if-let [terminal-val (get-in tm pre)]
        (if (map? terminal-val)
          (assoc-in tm s (assoc terminal-val nil nil))
          (-> tm
              (dissoc-in tm s)
              (assoc-in tm (rest s) {(last s) terminal-val,
                                     nil nil})))))))

(defn trie-add-seq
  "take a trie (represented as a nested map) and add a sequence"
  [trie-map s] 
  (loop [idx (count s)
         tm trie-map]
    (when-not (neg? idx)
      (let [[pre post] (split-at idx s)] 
        (if (get-in tm pre)
          (get-in tm pre)
          (recur (dec idx) tm))))))

(defn make-trie
  "take a sequence (may be nested) and creates a trie, represented as a nested map"
  [sequence]
  (let [s (flatten sequence)]
    (reduce trie-add-seq {} s)))
