(ns clj-thamil.format.convert-test
  (:require [clojure.test.check :as sc]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.properties :as prop :include-macros true]
            [clojure.string :as string]
            [clj-thamil.format :as fmt]
            [clj-thamil.format.convert :as cvt]
            [clj-thamil.மொழியியல் :as மொ])
  (:use clojure.test
        clj-thamil.format.convert))

(def QCHK-SIZE 100)

(def A_LOT 100)

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
    (is (not= "anbu" (தமிழ்->romanized "அன்பு")))))

(deftest double-check-test
  (testing "from the test.check / double-check Readme"
    (is (:result
         (sc/quick-check QCHK-SIZE (prop/for-all [v (gen/vector gen/int)]
                                           (= (sort v) (sort (sort v)))))))))

(deftest convert-fn-invertible
  (let [thamil-letters fmt/letter-seq
        punct (map str [\. \space \newline])
        all-letters (concat thamil-letters punct)
        lett-gen (gen/such-that identity (gen/elements all-letters))
        thamil-text-gen (gen/fmap string/join (gen/vector lett-gen))
        old-font-no-ambig-combo (fn [s]
                                  (let [phonemes (fmt/str->phonemes s)
                                        phoneme-triples (partition 3 1 phonemes)
                                        phoneme-doubles (partition 2 1 phonemes)
                                        ambig1 (fn [[a b c]] (and (மொ/மெய்யெழுத்தா? a)
                                                                 (= "எ" b)
                                                                 (= "ள்" c)))
                                        ambig2 (fn [[a b]] (and (= "ஒ" a)
                                                               (= "ள்" b)))
                                        ambig3 (fn [[a b c]] (and (மொ/மெய்யெழுத்தா? a)
                                                                 (#{"எ" "ஏ" "ஆ"} b)
                                                                 (= "ர்" c)))
                                        no-ambig1 (every? false? (map ambig1 phoneme-triples))
                                        no-ambig2 (every? false? (map ambig2 phoneme-doubles))
                                        no-ambig3 (every? false? (map ambig3 phoneme-doubles))]
                                    (and no-ambig1 no-ambig2 no-ambig3)))
        non-romanized-thamil-text-gen (gen/such-that old-font-no-ambig-combo lett-gen (* QCHK-SIZE A_LOT))
        test-prop (fn [f inv] (prop/for-all [t non-romanized-thamil-text-gen]
                                            (= t (-> t f inv))))
        test-res (fn [f inv]
                   (->> (test-prop f inv)
                        (sc/quick-check QCHK-SIZE)
                        :result))
        romanized-test-prop (prop/for-all [t thamil-text-gen]
                                          (let [converted-test-txt (-> t cvt/தமிழ்->romanized cvt/romanized->தமிழ்)]
                                            (= converted-test-txt (-> converted-test-txt cvt/தமிழ்->romanized cvt/romanized->தமிழ்))))
        romanized-res (->> romanized-test-prop
                           (sc/quick-check QCHK-SIZE)
                           :result)]
    (testing "convert and inverse fns for all font formats (except romanized)"
      (testing "romanized"
        (is (true? romanized-res)))
      (testing "tab"
        (is (true? (test-res cvt/தமிழ்->tab cvt/tab->தமிழ்))))
      (testing "bamini"
        (is (true? (test-res cvt/தமிழ்->bamini cvt/bamini->தமிழ்))))
      (testing "tscii"
        (is (true? (test-res cvt/தமிழ்->tscii cvt/tscii->தமிழ்))))
      (testing "webulagam"
        (is (true? (test-res cvt/தமிழ்->webulagam cvt/webulagam->தமிழ்)))))))
