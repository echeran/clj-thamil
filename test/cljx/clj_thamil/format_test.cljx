(ns clj-thamil.format-test
  (:use clojure.test
        clj-thamil.format
        clj-thamil.core))

(def words ["பந்து" "பந்தி" "பத்து" "பந்துகள்" "பந்தயம்" "பந்தாடு" "பந்தல்"])

(deftest trie-test
  (let [first-word (first words)
        first-two-words (take 2 words)]
    (testing "creating a trie"
      (testing "creating a trie from a sequence of words (default val is attached to terminus)"
        (testing "boundary case"
          (is (= {} (make-trie []))))
        (is (= {\ப {\ந {\u0BCD {\த {\u0BC1 {nil nil}}}}}} (make-trie [first-word])))
        (is (= (make-trie [first-word]) (make-trie (take 1 words))))
        (testing "words that share some prefix"
          (is (= {\ப {\ந {\u0BCD {\த {\u0BC1 {nil nil} \u0BBF {nil nil}}}}}} (make-trie first-two-words))))
        (testing "words that have no shared prefix"
          (is (= {\ப {\ந {\u0BCD {\த {\u0BC1 {nil nil}}}}} \வ {\u0BC6 {\ற {\u0BCD {\ற {\u0BBF {nil nil}}}}}}} (make-trie [first-word "வெற்றி"])))))
      (testing "creating a trie from a map of word->terminus-attached-val"
        (testing "boundary case"
          (is (= {} (make-trie {}))))
        (is (= {\ப {\ந {\u0BCD {\த {\u0BC1 {nil 1}}}}}} (make-trie {first-word 1})))
        (is (= {\ப {\ந {\u0BCD {\த {\u0BC1 {nil 0
                                            \க {\ள {\u0BCD {nil 3}}}}
                                    \u0BBF {nil 1}
                                    \ய {\ம {\u0BCD {nil 4}}}
                                    \ல {\u0BCD {nil 6}}
                                    \u0BBE {\ட {\u0BC1 {nil 5}}}}}}
                    \த {\u0BCD {\த {\u0BC1 {nil 2}}}}}}
               (make-trie (zipmap words (range)))))))
    (testing "trie lookup fns"
      (testing "nil as valued attached to terminus of input sequences"
        (is (= true (in-trie? {\ப {\ந {\u0BCD {\த {\u0BC1 {\க {\ள {\u0BCD {nil nil}}}}}}}}} "பந்துகள்")))
        (is (= false (in-trie? {\ப {\ந {\u0BCD {\த {\u0BC1 {\க {\ள {\u0BCD {nil nil}}}}}}}}} "ப")))
        (is (= false (in-trie? {\ப {\ந {\u0BCD {\த {\u0BC1 {\க {\ள {\u0BCD {nil nil}}}}}}}}} "பந்துக")))
        (is (= false (in-trie? {\ப {\ந {\u0BCD {\த {\u0BC1 {\க {\ள {\u0BCD {nil nil}}}}}}}}} "பந்து")))
        (is (= false (in-trie? {\ப {\ந {\u0BCD {\த {\u0BC1 {nil nil}}}}}} "பந்துகள்")))
        (is (= false (in-trie? {\ப {\ந {\u0BCD {\த {\u0BC1 {nil nil}}}}}} "ப")))
        (is (= false (in-trie? {\ப {\ந {\u0BCD {\த {\u0BC1 {nil nil}}}}}} "பந்துக")))
        (is (= true (in-trie? {\ப {\ந {\u0BCD {\த {\u0BC1 {nil nil}}}}}} "பந்து")))
        (is (= false (nil? (trie-prefix-subtree {\ப {\ந {\u0BCD {\த {\u0BC1 {nil nil}}}}}} "பந்து"))))
        (is (= false (nil? (trie-prefix-subtree {\ப {\ந {\u0BCD {\த {\u0BC1 {nil nil}}}}}} "ப"))))
        (is (= true (nil? (trie-prefix-subtree {\ப {\ந {\u0BCD {\த {\u0BC1 {nil nil}}}}}} "பந்துகள்"))))
        (is (= true (nil? (trie-prefix-subtree {\ப {\ந {\u0BCD {\த {\u0BC1 {nil nil}}}}}} "கோடு"))))) 
      (testing "non-nil values attached to terminus of input sequences"
        (is (= true (in-trie? {\ப {\ந {\u0BCD {\த {\u0BC1 {nil 1}}}}}} "பந்து")))
        (is (= false (in-trie? {\ப {\ந {\u0BCD {\த {\u0BC1 {nil 3.14159}}}}}} "ப")))
        (is (= false (nil? (trie-prefix-subtree {\ப {\ந {\u0BCD {\த {\u0BC1 {nil \a}}}}}} "பந்து"))))
        (is (= false (nil? (trie-prefix-subtree {\ப {\ந {\u0BCD {\த {\u0BC1 {nil true}}}}}} "ப"))))
        (testing "splitting words directly into phonemes using phoneme trie"
          (is (= ["வ்" "அ" "ண்" "அ" "க்" "க்" "அ" "ம்"] (str->letters phoneme-trie "வணக்கம்")))
          (is (empty? (str->letters phoneme-trie nil)))
          (is (empty? (str->letters phoneme-trie "")))
          (is (= ["அ"] (str->letters phoneme-trie "அ")))
          (is (= ["க்"] (str->letters phoneme-trie "க்")))
          (is (= ["க்" "அ"] (str->letters phoneme-trie "க")))
          (is (= ["க்" "ஊ"] (str->letters phoneme-trie "கூ")))
          (is (= ["வ்" "இ" "ட்" "உ" "த்" "அ" "ல்" "ஐ"] (str->letters phoneme-trie "விடுதலை"))))))))

(deftest word-letter-test
  (testing "splitting strings of தமிழ் characters into constituent தமிழ் characters"
    (is (= [] (str->letters "")))
    (is (= [] (str->letters nil)))
    (is (= ["த"] (str->letters "த")))
    (is (= [" " "த"] (str->letters " த")))
    (is (= ["த" " "] (str->letters "த ")))
    (is (= ["த்"] (str->letters "த்")))
    (is (= ["த" "மி" "ழ்"] (str->letters "தமிழ்")))
    (is (= ["த" "மி" "ழ்" " "] (str->letters "தமிழ் ")))
    (is (= ["s" "o" "f" "t" "w" "a" "r" "e" "=" "மெ" "ன்" "பொ" "ரு" "ள்" "," "." "." "."] (str->letters "software=மென்பொருள்,..."))))
  (testing "letter ordering"
    (testing "boundary cases"
      (is (= true (letter-before? nil nil)))
      (is (= true (letter-before? nil "")))
      (is (= false (letter-before? "" nil)))
      (is (= true (letter-before? nil "அ")))
      (is (= true (letter-before? nil "a")))
      (is (= true (letter-before? "a" "அ"))))
    (testing "equal inputs"
      (is (= false (letter-before? "அ" "அ"))))
    (testing "தமிழ்"
      (is (= true (letter-before? "அ" "ஆ")))
      (is (= true (letter-before? "ஆ" "இ")))
      (is (= true (letter-before? "அ" "ஔ")))
      (is (= true (letter-before? "ஔ" "ஃ")))
      (is (= true (letter-before? "ஃ" "க்")))
      (is (= true (letter-before? "க்" "க")))
      (is (= true (letter-before? "க" "கா")))
      (is (= true (letter-before? "க்" "கௌ")))
      (is (= false (letter-before? "க்" "ஃ")))
      (is (= true (letter-before? "கௌ" "ங்")))
      (is (= false (letter-before? "ங்" "கௌ"))))
    (testing "ASCII"
      (is (= true (letter-before? "a" "z")))
      (is (= true (letter-before? "A" "Z")))
      (is (= true (letter-before? "Z" "a")))
      (is (= true (letter-before? "0" "9")))
      (is (= true (letter-before? "9" "A"))))
    (testing "comparator / sorting"
      (is (= ["அ" "ஆ" "இ" "ஒ" "ஓ" "ஔ" "ஃ" "க்" "க" "ன்" "ன" "னா" "னு" "னௌ"] (sort-by identity letter-comp ["இ" "க" "ஃ" "ன" "னு" "னௌ" "னா" "ஆ" "க்" "அ" "ஔ" "ஓ" "ன்" "ஒ"])))))
  (testing "word ordering"
    (testing "equal inputs"
      (is (= false (word-before? "அ" "அ"))))
    (testing "extra letters in one word"
      (is (= false (word-before? "அது" "அ")))
      (is (= true (word-before? "அ" "அது"))))
    (testing "Unicode 'consonant' vs. Unicode 'consonant+ligature' - ஒருங்குறியில் தமிழ் மெய்யெழுத்து+அகரம் மற்றும் அதே மெய்யெழுத்து {வெறுமன்; அதோடு வேறொரு உயிரெழுத்து}"
      (is (= true (word-before? "படம்" "பாடம்")))
      (is (= false (word-before? "பாடம்" "படம்")))
      (is (= false (word-before? "படம்" "பட்டம்")))
      (is (= true (word-before? "பட்டம்" "படம்")))
      (is (= false (word-before? "கடமை" "கட்டம்")))
      (is (= true (word-before? "கட்டம்" "கடமை")))
      (is (= true (word-before? "கட்டு" "கெட்டு")))
      (is (= false (word-before? "கெட்டு" "கட்டு")))
      (is (= false (word-before? "பைந்தமிழ்" "பந்தல்")))
      (is (= true (word-before? "பந்தல்" "பைந்தமிழ்"))))
    (testing "order of consonants"
      (is (= true (word-before? "பாடம்" "பாதம்")))
      (is (= false (word-before? "பாதம்" "பாடம்"))))
    (testing "order of vowels"
      (is (= true (word-before? "அப்பம்" "ஆப்பம்")))
      (is (= false (word-before? "ஆப்பம்" "அப்பம்"))))
    (testing "order of vowel vs. consonant, and order of two உயிர்மெய்யெழுத்துகள்"
      (is (= false (word-before? "நுளம்பு" "கொசு")))
      (is (= true (word-before? "கொசு" "நுளம்பு")))
      (is (= true (word-before? "ஈ" "கொசு")))
      (is (= false (word-before? "கொசு" "ஈ"))))))

(deftest util-fn-test
  (let [s "abcqwertyuiop"]
    (testing "seq-prefix" 
      (is (= [] (seq-prefix nil nil)))
      (is (= [] (seq-prefix nil [])))
      (is (= [] (seq-prefix [] nil)))
      (is (= [] (seq-prefix nil [1 2])))
      (is (= [\a \b \c] (seq-prefix "abcdefgh" s)))
      (is (= [\a \b] (seq-prefix "abbb" s)))
      (is (= [] (seq-prefix "zyx" s))))
    (testing "seq-prefix?" 
      (is (= false (seq-prefix? nil nil)))
      (is (= false (seq-prefix? nil [])))
      (is (= false (seq-prefix? [] nil)))
      (is (= false (seq-prefix? nil [1 2])))
      (is (= false (seq-prefix? "abcdefgh" s)))
      (is (= false (seq-prefix? "abbb" s)))
      (is (= false (seq-prefix? "zyx" s))) 
      (is (= false (seq-prefix? "abc" s))) 
      (is (= true (seq-prefix? s "abc")))
      (is (= true (seq-prefix? s "a")))
      (is (= true (seq-prefix? s "")))
      (is (= true (seq-prefix? s [])))
      (is (= true (seq-prefix? s nil))))
    (testing "seq-index-of"
      (let [check-seq-index-of (fn [s1 s2] (= (.indexOf s1 s2)
                                         (seq-index-of s1 s2)))]
        (is (= true (check-seq-index-of "abc" "a")))
        (is (true? (check-seq-index-of "a" "abc")))
        (is (true? (check-seq-index-of "" "abc")))))
    (testing "prefix?" 
      (is (true? (prefix? "வந்தான்" "")))
      (is (true? (prefix? "வந்தான்" "வ்")))
      (is (true? (prefix? "வந்தான்" "வ")))
      (is (true? (prefix? "வந்தான்" "வந்")))
      (is (false? (prefix? "வந்தான்" "வந")))
      (is (true? (prefix? "வந்தான்" "வந்த்")))
      (is (false? (prefix? "வந்தான்" "வந்து")))
      (is (true? (prefix? "வந்தான்" "வந்தா")))
      (is (true? (prefix? "வந்தான்" "வந்தான்")))
      (is (false? (prefix? "வந்தான்" "வந்தானே")))
      (is (true? (prefix? "வந்தானே" "வந்தான்"))))))

(deftest word-char-traits-test 
  (testing "word and char traits"
    (testing "char traits"
      (let [ws-chars [\space \tab \newline]
            wordy-chars [\a \Z \0 ]
            punct-chars [\- \* \^ \$ \+ \. \_ \; ]
            தமிழ்-எழுத்து-unicode-chars [\அ \ஆ \இ \ஔ \ஃ \க \ங \ன]
            தமிழ்-எழுத்து-துணை-குறி-unicode-chars [\u0BCD \u0BBE \u0BBF \u0BC0 \u0BC1 \u0BC2 \u0BC6 \u0BC7 \u0BC8 \u0BCA \u0BCB \u0BCC]]
        (is (= true (every? true? (map whitespace? ws-chars))))
        (is (= true (every? true? (map wordy-char? wordy-chars))))
        (is (= true (every? true? (map wordy-char? தமிழ்-எழுத்து-unicode-chars))))
        (is (= true (every? true? (map wordy-char? தமிழ்-எழுத்து-துணை-குறி-unicode-chars))))
        (is (= true (every? true? (map wordy-char? தமிழ்-எழுத்து-துணை-குறி-unicode-chars))))
        (is (= true (every? false? (map wordy-char? punct-chars))))))
    (testing "word boundaries"
      (let [s1 "aldsk சிக்கல் sdfsdf234234lsdflksjdf Zürich"
            s2 "  alsfjs"
            s3 ""
            s4 nil]
        (is (= ["aldsk" "சிக்கல்" "sdfsdf234234lsdflksjdf" "Zürich"] (wordy-seq s1)))
        (is (= ["alsfjs"] (wordy-seq s2)))
        (is (= [] (wordy-seq s3)))
        (is (= nil (wordy-seq s4)))))))

(deftest cursor-pos-test
  (let [s1 "aldsk சிக்கல் sdfsdf234234lsdflksjdf Zürich"
        s2 "  alsfjs"
        s3 "a    b"]
    (testing "cursor position"
      (testing "wordy chunk under cursor"
        (is (= "aldsk" (wordy-chunk-under s1 0)))
        (is (= "aldsk" (wordy-chunk-under s1 1)))
        (is (= "aldsk" (wordy-chunk-under s1 5)))
        (is (= "சிக்கல்" (wordy-chunk-under s1 6)))
        (is (= "Zürich" (wordy-chunk-under s1 (count s1))))
        (is (= "Zürich" (wordy-chunk-under s1 (- (count s1) (count "Zürich")))))
        (is (nil? (wordy-chunk-under s2 0)))
        (is (nil? (wordy-chunk-under s2 1)))
        (is (= "alsfjs" (wordy-chunk-under s2 2)))
        (is (= "a" (wordy-chunk-under s3 0)))
        (is (= "a" (wordy-chunk-under s3 1)))
        (is (nil? (wordy-chunk-under s3 2))))
      (testing "cursor position within wordy chunk"
        (is (= ["aldsk" 0] (wordy-chunk-and-cursor-pos s1 0)))
        (is (= ["aldsk" 1] (wordy-chunk-and-cursor-pos s1 1)))
        (is (= ["aldsk" 5] (wordy-chunk-and-cursor-pos s1 5)))
        (is (= ["சிக்கல்" 0] (wordy-chunk-and-cursor-pos s1 6)))
        (is (= ["Zürich" 6] (wordy-chunk-and-cursor-pos s1 (count s1))))
        (is (= ["Zürich" 0] (wordy-chunk-and-cursor-pos s1 (- (count s1) (count "Zürich")))))
        (is (nil? (wordy-chunk-and-cursor-pos s2 0)))
        (is (nil? (wordy-chunk-and-cursor-pos s2 1)))
        (is (= ["alsfjs" 0] (wordy-chunk-and-cursor-pos s2 2)))
        (is (= ["a" 0] (wordy-chunk-and-cursor-pos s3 0)))
        (is (= ["a" 1] (wordy-chunk-and-cursor-pos s3 1)))
        (is (nil? (wordy-chunk-and-cursor-pos s3 2)))
        ))))
