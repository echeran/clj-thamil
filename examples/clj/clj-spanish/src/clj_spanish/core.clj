(ns clj-spanish.core
  (:require [clj-thamil.core :refer [translate-fns translate-forms]]))

(def fns-map '{
               take toma
               drop baja
               inc carga ;; translated to "load" in english
               dec extracto ;; based soley on translation. need more info/context 
               				;; to decide what connotations/denotations would be best 
               range gama
               take-while toma-mientras
               drop-while baja-mientras
               interleave பின்னு ;; not sure what this means or what function
               					 ;; this macro serves, and thus can't decide how to label
               ;; reduce reduce
               ;; reducer reductor
               map mapa
               hash-map hachís-mapa 
               ;; vector vector 
               list enumera
               set pone 
               hash-set hachís-pone ;; could use fijo or colocar as "set"
               atom átomo
               agent agencia ;; or agente
               first primero
               second segundo
               last último
               butlast pero-último ;; not sure of what this should communicate.
               rest lodemás ;; should we separate the words into "lo demás" 
               				;; or shorten it to "demás"? Could also use "el resto"
               next próximo ;; this is used in the present, but "siguiente" is used
               				;; in the past. Not sure which makes more sense. 
               true cierto
               false falso
               print imprime 
               println imprimeln ;; ln means "line" in English, and 
               					  ;; line in spanish is simply "linea" so 
               					  ;; I thought it appropriate to keep it.
               filter forma
               remove quita
               keep guardar
               slurp ventosa;; if this should be a verb, use "sorber"
               spit escupe ;; could be "saliva" if it's not an action
               seq sec ;; short for "secuenciar"
               dorun hazcorrer ;; could also just use "haz" meaning "do"
               doall haztodo ;; literally means do it all
               str crd ;; short for "cuerda" which translates to string
               interpose interpone 
               find encuentra 
               get consigue 
               apply aplica
               count cuenta
               every? cada?
               true? cierto?
               false? falso?
               concat social
               identity identidad
               reverse invierte
               some alguno
               flatten aplana

               boolean booleano ;; sounds like English but couldn't find
               					;; a more specific word
               })

(def forms-map '{
                 if si
                 when cuando
                 if-not si-no
                 when-not cuando-no
                 ;; def def ;; short for "definir" = define
                 ;; fn fn ;; short for "función" 
                 ;; defn defn ;; again, it still makes sense to keep the 
                 		   ;; English equivalents because romance languages
                 		   ;; can sometimes have the same abbreviations
                 let deja
                 and y
                 or o
                 not no
                 else más ;; or "otro"
                 loop darvuelta ;; actually two words "dar vuelta"
                 doseq hazsec
                 for para ;; could be por, but I think para fits the function better
                 cond dependela ;; means "depending on the ..." --> dependiendo de la
                 do haz
                 
                 ;; clojure.test
                 deftest def-prueba ;; not sure if I should hyphenate all the
                 					;; double words, or keep as a compound?
                 testing probando 
                 is es
                 are son
                })

;; do the actual "translation" for bindings, fns, and any other value
(translate-fns fns-map)

;; do the actual "translation" for macros and special forms
(translate-forms forms-map)
