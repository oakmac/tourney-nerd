(defproject tourney-nerd "0.1.0"

  :description "Handle logic for Ultimate tournaments."
  :url "https://github.com/oakmac/tourney-nerd"

  :license {:name "ISC License"
            :url "https://github.com/oakmac/tourney-nerd/blob/master/LICENSE.md"
            :distribution :repo}

  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/clojurescript "1.9.293"]]

  :plugins [[lein-cljsbuild "1.1.5"]]

  :source-paths ["src"]

  :clean-targets ["app.js"]

  :cljsbuild
    {:builds
      [{:id "admin-dev"
        :source-paths ["cljs-admin" "cljs-common"]
        :compiler {:output-to "public/js/admin-dev.js"
                   :optimizations :whitespace}}

       {:id "admin-prod"
        :source-paths ["cljs-admin" "cljs-common"]
        :compiler {:output-to "public/js/admin.min.js"
                   :optimizations :advanced
                   :pretty-print false}}

       {:id "client-dev"
        :source-paths ["cljs-client" "cljs-common"]
        :compiler {:output-to "public/js/client-dev.js"
                   :optimizations :whitespace}}

       {:id "client-prod"
        :source-paths ["cljs-client" "cljs-common"]
        :compiler {:output-to "public/js/client.min.js"
                   :optimizations :advanced
                   :pretty-print false}}]})
