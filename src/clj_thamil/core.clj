(ns clj-thamil.core)

(def f "outfile.txt")

(def s "வணக்கம்")



(defn hello
  []
  (str "Hello, and " s))

(println (hello))


(defn print-hello
  [file-name]
  (spit file-name (hello)))

(def ஒன்று 1)
(def இரண்டு 2)

(comment
ஒன்று
இரண்டு
)

(+ ஒன்று இரண்டு)

(def v [ஒன்று இரண்டு "மூன்று"])


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

;; (def எனில் if)
;; (def என்றபோது when)
;; (def இல்லெனில் if-not)
;; (def இல்லென்ற-போது when-not)
;; (def வரையறு def)
;; (defmacro எனில்
;;   [& body]
;;   `(if ~@body))
(defmacro translate-form
  [old-name new-name]
  `(defmacro ~new-name
     [~'& body#]
     `(~'~old-name ~@body#)))
(translate-form if எனில்)
