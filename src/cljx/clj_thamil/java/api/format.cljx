(ns clj-thamil.java.api.format
  (:require [clj-thamil.format :as fmt])
  (:import java.util.Comparator)
  (:gen-class
   :methods [#^{:static true} [word_comp [] java.util.Comparator]]))

(defn -word_comp [] fmt/word-comp)
