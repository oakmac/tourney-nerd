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

  :source-paths ["src-cljc"]

  :test-paths ["test/"]

  :clean-targets ["target/"]

  :profiles
  {:dev {:dependencies [[metosin/jsonista "0.2.6"]]
          :resource-paths ["test-resources/"]
          :source-paths ["dev" "src-cljc"]}})
