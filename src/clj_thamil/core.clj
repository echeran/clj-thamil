(ns clj-thamil.core)

(def எடு take)
(def விடு drop)
(def ஏறுமானம் inc)
(def ஏற்று inc)
(def இறங்குமானம் dec)
(def இறக்கு dec)
(def வீச்சு range)
(def எடு-என்னும்போது take-while)
(def விடு-என்னும்போது drop-while)
(def பின்னு interleave)
(def இறுக்கு reduce)
;; இறுக்குவர் - reducer?
(def விவரி map)
;; (def புலவெண்-விவரம் hash-map)
(def புலவெண்-விவரணையாக்கம் hash-map)
;; (def காவி vector) ?
;; (def நெறியம் vector) ?
(def பட்டியல் list)
(def அமைவு set)
(def புலவெண்-அமைவு hash-set)
(def அணு atom)
(def முகவர் agent)
(def முதல் first)
(def இரண்டாம் second)
(def கடைசி last)
(def கடைசியின்றி butlast)
(def மீதி rest)
(def அடுத்த next)

(defmacro translate-fn
  [old-name new-name]
  `(def ~old-name ~new-name))


;; info on macro-writing macros based on info at
;; http://amalloy.hubpages.com/hub/Clojure-macro-writing-macros

(defmacro translate-form
  "Does the effective translation of a special form or macro from its old name to its new name.  In other words, generalizes the 'manual' process of defining something like:
(defmacro எனில்
  [& body]
  `(if ~@body))"
  [old-name new-name]
  `(defmacro ~new-name
     [~'& body#]
     `(~'~old-name ~@body#)))

(translate-form if எனில்)
(translate-form when என்றபோது)
(translate-form if-not இல்லெனில்)
(translate-form when-not இல்லென்ற-போது)
(translate-form def வரையறு)
