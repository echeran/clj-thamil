(ns clj-thamil.format-test
  (:use clojure.test
        clj-thamil.format
        clj-thamil.core))

(def words ["பந்து" "பந்தி" "பத்து" "பந்துகள்" "பந்தயம்" "பந்தாடு" "பந்தல்"])

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
    (is (= ["s" "o" "f" "t" "w" "a" "r" "e" "=" "மெ" "ன்" "பொ" "ரு" "ள்" "," "." "." "."] (str->letters "software=மென்பொருள்,...")))))


