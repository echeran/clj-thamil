(ns clj-thamil.format.convert-test
  (:use clojure.test
        clj-thamil.format.convert))

(deftest conversion-test
  (testing "romanized -> தமிழ்"
    (is (= "தமிழ்" (romanized->தமிழ் "thamiz")))
    (is (= "தமிழ்" (romanized->தமிழ் "thamizh")))
    (is (= "நீர்" (romanized->தமிழ் "n-iir")))
    (is (= "பஃறுளி" (romanized->தமிழ் "paqRuLi")))
    (is (= "சின்ன" (romanized->தமிழ் "chinna") (romanized->தமிழ் "sinna")))
    (is (= "விகடன்" (romanized->தமிழ் "vikatan") (romanized->தமிழ் "vikadan")))
    (is (= "சென்றேன் வென்றேன்" (romanized->தமிழ் "senreen venreen")))
    (is (= "வந்தேன்" (romanized->தமிழ் "vantheen")))
    (is (= "பாட்டு பாடு" (romanized->தமிழ் "paattu paadu"))))
  (testing "தமிழ் -> romanized; translation map inversion"
    (is (= "thamizh" (தமிழ்->romanized "தமிழ்")))
    (is (not= "thamiz" (தமிழ்->romanized "தமிழ்")))
    (is (= "niir" (தமிழ்->romanized "நீர்")))
    (is (not= "neer" (தமிழ்->romanized "நீர்")))
    (is (= "paambu" (தமிழ்->romanized "பாம்பு")))
    (is (not= "paampu" (தமிழ்->romanized "பாம்பு")))
    (is (= "anpu" (தமிழ்->romanized "அன்பு")))
    (is (not= "anbu" (தமிழ்->romanized "அன்பு")))
    ))
