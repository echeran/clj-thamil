(defproject clj-thamil "0.2.0"
  :description "A project encompassing various Thamil language-specific computing ideas"
  :url "https://github.com/echeran/clj-thamil"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :scm {:name "git"
        :url "https://github.com/echeran/clj-thamil"}
  :repositories [["releases" {:url "https://clojars.org/repo/"}]]
  :deploy-repositories [["clojars" {:creds :gpg}]]
  :pom-addition [:developers [:developer
                              [:name "Elango Cheran"]
                              [:url "http://www.elangocheran.com"]
                              [:email "elango.cheran@gmail.com"]
                              [:timezone "-8"]]] 

  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/data.csv "0.1.2"]
                 [org.clojure/algo.generic "0.1.2"]
                 [org.clojure/test.check "0.9.0"]
                 [org.clojure/clojurescript "1.9.89"]]
  
  :jar-exclusions [#"\.cljx|\.swp|\.swo|\.DS_Store"]

  :aot [clj-thamil.main
        clj-thamil.java.api.format
        clj-thamil.format.analysis
        clj-thamil.format.convert]

  :main clj-thamil.main  

  :lein-release {:deploy-via :shell
                 :shell ["lein" "deploy"]}

  :profiles {:provided {:dependencies []}
             :dev {:plugins [[lein-cljsbuild "1.1.3"]] 
                   ;; :cljsbuild {:test-commands {"node" ["node" :node-runner "target/testable.js"]}
                   ;;             :builds [{:source-paths ["target/classes" "target/test-classes"]
                   ;;                       :compiler {:output-to "target/testable.js"
                   ;;                                  :optimizations :advanced
                   ;;                                  :pretty-print true}}]}
                   
                   :cljsbuild {:builds {:app {:source-paths ["src"]
                                              :compiler {:output-to     "resources/public/js/clj-thamil.js"
                                                         :output-dir    "resources/public/js/out"
                                                         :externs       []
                                                         :optimizations :none
                                                         :pretty-print  true}}}
                               :test-commands {
                                               ;; no cljs test configured yet
                                               }}}})
