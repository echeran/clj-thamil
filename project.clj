(defproject clj-thamil "0.2.0-SNAPSHOT"
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

  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/data.csv "0.1.2"]
                 [org.clojure/algo.generic "0.1.2"]
                 [com.cemerick/double-check "0.6.1"]]
  ;; this config seems to work with clojure 1.5.1 but may not 1.6.0
  ;; (if not, then I don't know why)
  
  :jar-exclusions [#"\.cljx|\.swp|\.swo|\.DS_Store"]
  :source-paths ["src" "target/generated/src"]
  :resource-paths ["target/generated/src"]
  :test-paths ["target/generated/test"]

  :plugins [[lein-cljsbuild "1.0.3"]]

  :aot [clj-thamil.main
        clj-thamil.java.api.format
        clj-thamil.format.analysis
        clj-thamil.format.convert]

  :main clj-thamil.main

  :cljx {:builds [{:source-paths ["src/cljx"]
                   :output-path "target/generated/src"
                   :rules :clj}

                  {:source-paths ["src/cljx"]
                   :output-path "target/generated/src"
                   :rules :cljs}
                  
                  {:source-paths ["test/cljx"]
                   :output-path "target/generated/test"
                   :rules :clj}

                  {:source-paths ["test/cljx"]
                   :output-path "target/generated/test"
                   :rules :cljs}]}

  :cljsbuild {:test-commands {"node" ["node" :node-runner "target/testable.js"]}
              :builds [{:source-paths ["target/classes" "target/test-classes"]
                        :compiler {:output-to "target/testable.js"
                                   :optimizations :advanced
                                   :pretty-print true}}]}
  
  :hooks [cljx.hooks]

  :aliases {"test-cljs" ["do" ["cljx" "once"] ["cljsbuild" "test"]]
            "test-all"  ["do" ["with-profile" "default:+1.6" "test"] ["cljsbuild" "test"]]}

  :lein-release {:deploy-via :shell
                 :shell ["lein" "deploy"]}

  :profiles {:dev {:plugins [[com.keminglabs/cljx "0.4.0"]]
                   :aliases {"cleantest" ["do" "clean," "cljx" "once," "test,"
                                          "cljsbuild" "test"]
                             "deploy" ["do" "clean" ["cljx" "once"] ["deploy" "clojars"]]}}
             :provided {:dependencies [[org.clojure/clojurescript "0.0-2234"]]}})
