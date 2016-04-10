(ns clj-spanish.core-test
  (:require [clojure.test :refer :all]
            [clj-spanish.core :refer :all]))

(deftest core-test
  (let [numbers [2 3 5 7 11]]
    (testing "Clojure en español"
      (is (= 11 (último numbers))))))
