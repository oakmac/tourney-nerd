(ns tourney-nerd.teams
  "Functions that operate on teams.")

(defn teams->sorted-by-seed
  "Convert teams into a list ordered by their seed."
  [teams]
  (let [teams (if (map? teams) (vals teams) teams)]
    (assert (sequential? teams) "Non-sequential value for teams passed to teams->sorted-by-seed")
    (sort-by :seed teams)))
