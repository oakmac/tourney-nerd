(defproject tourney-nerd "0.1.0"

  :description "Handle logic for Ultimate tournaments."
  :url "https://github.com/oakmac/tourney-nerd"

  :license {:name "ISC License"
            :url "https://github.com/oakmac/tourney-nerd/blob/master/LICENSE.md"
            :distribution :repo}

  :dependencies
    [[org.clojure/clojure "1.10.1"]
     [org.clojure/clojurescript "1.10.597"]
     [com.taoensso/timbre "4.10.0"]]

  :plugins [[lein-cljsbuild "1.1.7"]]

  :source-paths ["src-cljc"]

  :repl-options {:init-ns tourney-nerd.round-robin-pool}

  :clean-targets ["target" "tourney-nerd.js"]

  :cljsbuild
    {:builds
      [{:id "main"
        :source-paths ["src-cljs" "src-cljc"]
        :compiler {:language-in :ecmascript5
                   :language-out :ecmascript5
                   :output-to "tourney-nerd.js"
                   :optimizations :simple
                   :target :nodejs}}]})
