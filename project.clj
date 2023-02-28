(defproject tourney-nerd "0.1.0"

  :description "Handle logic for Ultimate tournaments."
  :url "https://github.com/oakmac/tourney-nerd"

  :license {:name "ISC License"
            :url "https://github.com/oakmac/tourney-nerd/blob/master/LICENSE.md"
            :distribution :repo}

  :dependencies
  [[org.clojure/clojure "1.11.1"]
   [org.clojure/clojurescript "1.11.60"]
   [com.taoensso/timbre "5.2.1"]
   [metosin/malli "0.3.1"]]

  :plugins [[lein-cljsbuild "1.1.7"]]

  :source-paths ["src-cljc"]

  :test-paths ["test/"]

  :clean-targets ["target/" "tourney-nerd.js"]

  :profiles
  {:repl {:dependencies [[metosin/jsonista "0.2.6"]]
          :source-paths ["dev" "src-cljc"]}}

  :cljsbuild
  {:builds
    [{:id "main"
      :source-paths ["src-cljs" "src-cljc"]
      :compiler {:language-in :ecmascript5
                 :language-out :ecmascript5
                 :output-to "tourney-nerd.js"
                 :optimizations :simple
                 :target :nodejs}}]})
