(defproject tourney-nerd "0.1.0"

  :description "Handle logic for Ultimate tournaments."
  :url "https://github.com/oakmac/tourney-nerd"

  :license {:name "ISC License"
            :url "https://github.com/oakmac/tourney-nerd/blob/master/LICENSE.md"
            :distribution :repo}

  :dependencies
    [[org.clojure/clojure "1.9.0"]
     [org.clojure/clojurescript "1.10.339"]]

  :plugins [[lein-cljsbuild "1.1.7"]]

  :source-paths ["src"]

  :clean-targets ["target" "tourney-nerd.js"]

  :cljsbuild
    {:builds
      [{:id "main"
        :source-paths ["src-cljs"]
        :compiler {:language-in :ecmascript5
                   :language-out :ecmascript5
                   :output-to "tourney-nerd.js"
                   :optimizations :simple
                   :target :nodejs}}]})
