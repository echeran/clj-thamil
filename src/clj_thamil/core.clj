(ns clj-thamil.core)

(def எடு take)
(def விடு drop)
(def ஏறுமானம் inc)
(def ஏற்று inc)
(def இறங்குமானம் dec)
(def இறக்கு dec)
(def வீச்சு range)
(def எடு-என்னும்வரை take-while)
(def விடு-என்னும்வரை drop-while)
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
(def வாய்மை true) ;; should we use வாய்மை, மெய்மை, or உண்மை ?  i am
;; thinking of using வாய்மை or மெய்மை so as to leave உண்மை to continue to
;; be used in more casual / less formal situations
(def பொய்மை false)
(def அச்சிடு print)
(def வரி-அச்சிடு println)
(def வடி filter)
(def அகற்று remove)
(def கொள் keep)
(def உறிஞ்சு slurp) ;; could be சப்பு
(def ஊற்று spit) ;; could be துப்பு

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

(translate-form if எனில்) ;; எனில் is both short and not used literally
;; in casual conversation
(translate-form when என்னும்போது)
(translate-form if-not இல்லெனில்)
(translate-form when-not இல்லென்னும்-போது)
(translate-form def வரையறு)
(translate-form fn செகூ) ;; function -> செயல்கூறு
(translate-form defn வசெகூறு) ;; def + fn = defn -> வரையறு + செ(யல்)கூ(று)
;; = வசெகூறு
(translate-form let வை)

;; and மற்றும்
;; or அல்லது
;; not அன்று
;; else என்றேல் ??  does அன்றி make sense?

(defmacro translate-form-symbol
  "Does the effective translation of a special form or macro from its old name to its new name, with the names given as symbols"
  [old-name new-name]
  `(defmacro ~(eval new-name)
     [~'& body#]
     `(~'~(eval old-name) ~@body#)))

(defmacro translate-forms
  "takes a map of symbols and creates macros that do the translation of the form of the old symbol (key) to the new symbol (val)"
  [symb-map] 
  `(do
     ~@
     (for [[old-form# new-form#] (eval symb-map)]
        `(translate-form-symbol '~old-form# '~new-form#))))
