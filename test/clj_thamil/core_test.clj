(ns clj-thamil.core-test
  (:use clojure.test
        clj-thamil.core))

(deftest a-test
  (testing "FIXME, I fail."
    (let [hello (fn []
                  (str "Hello, and " s)) 
          ஒன்று 1
          இரண்டு 2 
          v [ஒன்று இரண்டு "மூன்று"]]
      (is (= v [1 2 "மூன்று"]))
      (is (= (hello) "Hello, and வணக்கம்")))))
