(ns ஆமை-தமிழ்.கரு
  (:require [clojure-turtle.core :as turtle])
  (:use clj-thamil.core))

(translate-forms '{translate-fns மொழிப்பெயர்-செயல்கூறுகள்
                   translate-forms மொழிப்பெயர்-வடிவங்கள்})

(வரையறு ஆமை-செயல்கூறுகள்
        '{turtle/forward முன்னால்
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

(வரையறு ஆமை-வடிவங்கள்
        '{turtle/repeat மீண்டும்
          turtle/all எல்லாம்
          turtle/new-window புது-சாளரம்})

(மொழிப்பெயர்-செயல்கூறுகள் ஆமை-செயல்கூறுகள்)
(மொழிப்பெயர்-வடிவங்கள் ஆமை-வடிவங்கள்)
