(ns clj-thamil.main
  (require [clojure.string :as string]
           [clj-thamil.format [analysis :as analysis] [convert :as convert]]
           [clj-thamil.subprograms :as subprog])
  (:gen-class))

(def ^{:doc "a map that specifies what sub-program to run based on the first arg passed in"}
  main-fns
  {"freqs" analysis/-main 
   "osxkeyb" convert/-main
   "phonemes" subprog/print-as-phonemes})

(defn -main [& args]
  (assert (pos? (count args)) "Running clj-thamil as an executable requires arguments")
  (let [subprog (first args)
        default-fn (fn [& args] (throw (Exception. (str "The specified clj-thamil sub-program is misspelled or does not exist.  Available sub-programs: [" (string/join ", " (-> main-fns keys sort)) "]"))))
        subprog-fn (get main-fns subprog default-fn)
        new-args (rest args)]
    (apply subprog-fn new-args)))
