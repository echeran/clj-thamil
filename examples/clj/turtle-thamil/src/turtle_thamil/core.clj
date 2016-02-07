(ns turtle-thamil.core
  (:require [clojure-turtle.core :as turtle])
  (:use clj-thamil.core))

(def turtle-fns-map '{turtle/forward முன்னால்
                      turtle/back பின்னால்
                      turtle/right வலது
                      turtle/left இடது
                      turtle/translate இடம்பெயர்
                      turtle/penup எழுதுகோலெடு
                      turtle/pendown எழுதுகோல்வை
                      turtle/clean துப்புரவு
                      ;; setxy ???
                      turtle/setheading திசைவை
                      turtle/home வீடு})

(def turtle-forms-map '{turtle/repeat மீண்டும்
                        turtle/all எல்லாம்
                        turtle/new-window புது-சாளரம்})

(translate-fns turtle-fns-map)
(translate-forms turtle-forms-map)
