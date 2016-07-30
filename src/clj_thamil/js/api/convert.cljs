(ns clj-thamil.js.api.convert
  (:require [clj-thamil.format.convert :as cvt]))

(def romanized-to-thamil cvt/romanized->தமிழ்)
(def thamil-to-romanized cvt/தமிழ்->romanized)

(def tab-to-thamil cvt/tab->தமிழ்)
(def thamil-to-tab cvt/தமிழ்->tab)

(def bamini-to-thamil cvt/bamini->தமிழ்)
(def thamil-to-bamini cvt/தமிழ்->bamini)

(def tscii-to-thamil cvt/tscii->தமிழ்)
(def thamil-to-tscii cvt/தமிழ்->tscii)

(def webulagam-to-thamil cvt/webulagam->தமிழ்)
(def thamil-to-webulagam cvt/தமிழ்->webulagam)


