(defproject tourney-nerd "0.1.0"

  :description "Handle logic for Ultimate tournaments."
  :url "https://github.com/oakmac/tourney-nerd"

  :license {:name "ISC License"
            :url "https://github.com/oakmac/tourney-nerd/blob/master/LICENSE.md"
            :distribution :repo}

  :dependencies
    [[org.clojure/clojure "1.8.0"]
     [org.clojure/clojurescript "1.9.456"]]

  :plugins [[lein-cljsbuild "1.1.5"]]

  :source-paths ["src"]

  :clean-targets ["tourney-nerd.js"]

  :cljsbuild
    {:builds
      [{:id "main"
        :source-paths ["src-cljs"]
        :compiler {:language-in :ecmascript5
                   :language-out :ecmascript5
                   :output-to "tourney-nerd.js"
                   :optimizations :simple
                   :target :nodejs}}]})
