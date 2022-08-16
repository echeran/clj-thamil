(ns clj-thamil.subprograms
  (:require [clojure.java.io :as jio]
            [clj-thamil.format :as fmt]))

(defn print-as-phonemes
  [& args]
  (with-open [rdr (jio/reader *in*)]
    (let [lines (line-seq rdr)]
      (doall
          (for [line lines]
            (let [phoneme-str (apply str (fmt/str->phonemes line))]
              (println phoneme-str)))))))
