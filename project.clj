(defproject clojure101 "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :dependencies [[hiccups "0.2.0"]
                 [joodo "2.1.0"]
                 [org.clojars.trptcolin/domina "1.0.2.1"] ; waiting on release including https://github.com/levand/domina/pull/65
                 [org.clojars.trptcolin/shoreleave-remote "0.3.0.1"] ; waiting on release including]
                 [org.clojure/clojure "1.5.1"]
                 [ring-server/ring-server "0.3.1"]
                 [shoreleave/shoreleave-remote-ring "0.3.0" :exclusions [[org.clojure/tools.reader]]]
                 ]

  :profiles {:dev {:dependencies [[speclj "2.8.1"]
                                  [specljs "2.8.1"]
                                  [org.clojure/clojurescript "0.0-2014"]]}
             :cljs {:repl-options {:nrepl-middleware [cemerick.piggieback/wrap-cljs-repl]
                                   :init-ns clojure101.main}
                    :dependencies [[specljs "2.8.1"]
                                   [com.cemerick/piggieback "0.0.4"]]}}
  :plugins [[speclj "2.8.1"]
            [lein-cljsbuild "1.0.0"]
            [lein-ring "0.8.8"]]

  :cljsbuild ~(let [run-specs ["bin/specljs"  "resources/public/javascript/clojure101_dev.js"]]
          {:builds {:dev {:source-paths ["src/cljs" "spec/cljs"]
                               :compiler {:output-to "resources/public/javascript/clojure101_dev.js"
                                          :optimizations :whitespace
                                          :pretty-print true}
                          :notify-command run-specs}
                     :prod {:source-paths ["src/cljs"]
                             :compiler {:output-to "resources/public/javascript/clojure101.js"
                                        :optimizations :simple}}}
            :test-commands {"test" run-specs}})

  :source-paths ["src/clj" "src/cljs"]
  :test-paths ["spec/clj"]
  :ring {:handler clojure101.main/app
         :init clojure101.init/init})
