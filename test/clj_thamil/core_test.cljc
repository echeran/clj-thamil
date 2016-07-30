(ns clj-thamil.core-test
  (:use clojure.test
        clj-thamil.core))

(defn demo-print-1
  []
  (println "hello"))

(defn demo-print-2
  []
  (println "வணக்கம்"))

(defn demo-print-3
  []
  (வரி-அச்சிடு "வணக்கம்"))

(defn demo-add-1
  []
  (if (= 4 (+ 2 2))
    (println "true")
    (println "false")))

(defn demo-add-2
  []
  (if (= 4 (+ 2 2))
    (println "true")
    (println "false"))
  (if (= 5 (+ 2 2))
    (println "true")
    (println "false")))

(defn demo-add-3
  []
  (எனில் (= 4 (+ 2 2))
        (வரி-அச்சிடு "வாய்மை")
        (வரி-அச்சிடு "பொய்மை"))
  (எனில் (= 5 (+ 2 2))
        (வரி-அச்சிடு "வாய்மை")
        (வரி-அச்சிடு "பொய்மை")))

(வரையறு-செயல்கூறு demo-add-4
  []
  (எனில் (= 4 (+ 2 2))
        (வரி-அச்சிடு "வாய்மை")
        (வரி-அச்சிடு "பொய்மை"))
  (எனில் (= 5 (+ 2 2))
        (வரி-அச்சிடு "வாய்மை")
        (வரி-அச்சிடு "பொய்மை")))

(வரையறு-செயல்கூறு மாதிரி-கூட்டு-5
  []
  (எனில் (= 4 (+ 2 2))
        (வரி-அச்சிடு "வாய்மை")
        (வரி-அச்சிடு "பொய்மை"))
  (எனில் (= 5 (+ 2 2))
        (வரி-அச்சிடு "வாய்மை")
        (வரி-அச்சிடு "பொய்மை")))

(def demo-fns [demo-print-1 demo-print-2 demo-print-3
               demo-add-1 demo-add-2 demo-add-3 demo-add-4 மாதிரி-கூட்டு-5])

(deftest a-test
  (testing "FIXME, I fail."
    (let [s "வணக்கம்"
          hello (fn []
                  (str "Hello, and " s)) 
          ஒன்று 1
          இரண்டு 2 
          v [ஒன்று இரண்டு "மூன்று"]]
      (is (= v [1 2 "மூன்று"]))
      (is (= (hello) "Hello, and வணக்கம்")))))

