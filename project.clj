(defproject com.oakmac/tourney-nerd "0.15.0"

  :description "Handle logic for Ultimate tournaments."
  :url "https://github.com/oakmac/tourney-nerd"

  :license {:name "ISC License"
            :url "https://github.com/oakmac/tourney-nerd/blob/master/LICENSE.md"
            :distribution :repo}

  :dependencies
  [[org.clojure/clojure "1.12.6"]]

  :source-paths ["src-cljc"]

  :test-paths ["test/"]

  :clean-targets ["target/"]

  :profiles
  {:dev {:dependencies [[metosin/jsonista "0.3.8"]]
          :resource-paths ["test-resources/"]
          :source-paths ["dev" "src-cljc"]}})
