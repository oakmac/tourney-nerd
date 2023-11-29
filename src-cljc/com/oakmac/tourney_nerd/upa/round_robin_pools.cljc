(ns com.oakmac.tourney-nerd.upa.round-robin-pools
  "Round-robin pools from the UPA Tournament Manual")

(def table-3-1
  "Table 3.1 - Three-Team Round-robin"
  [["1v3"]
   ["2v3"]
   ["1v2"]])

(def table-4-1
  "Table 4.1 - pre-set four-team round-robin"
  [["1v3" "2v4"]
   ["1v4" "2v3"]
   ["1v2" "3v4"]])

;; TODO: need to figure out how to represent this format
;; vector of vectors is not going to work
; (def table-4-2
;   "Table 4.2 - The flexible four-team round-robin"
;   [["1v3" "2v4"]
;    ["1v4" "2v3"]
;    ["1v2" "3v4"]])

(def table-5-1-1
  "Table 5.1.1 - Five-team round-robin - version 1"
  [["1v2" "3v4"]
   ["1v4" "2v5"]
   ["1v3" "4v5"]
   ["2v4" "3v5"]
   ["2v3" "1v5"]])

(def table-5-1-2
  "Table 5.1.2 - Five-team round-robin - version 2"
  [["4v5" "2v3"]
   ["1v3" "2v4"]
   ["1v5" "3v4"]
   ["1v4" "2v5"]
   ["1v2" "3v5"]])

(def table-5-1-3
  "Table 5.1.3 - Five-team round-robin - version 3"
  [["1v5" "3v4"]
   ["1v3" "2v4"]
   ["4v5" "2v3"]
   ["1v4" "2v5"]
   ["1v2" "3v5"]])

(def table-5-1-4
  "Table 5.1.4 - Five-team round-robin - version 4"
  [["1v2" "4v5"]
   ["1v4" "3v5"]
   ["2v5" "3v4"]
   ["2v4" "1v3"]
   ["2v3" "1v5"]])

(def table-6-1-1
  "Table 6.1.1 - Six-team round-robin – version 1:
   The following schedule is played in one day. Games are played to 11."
  [["1v3" "2v5" "4v6"]
   ["1v5" "2v4" "3v6"]
   ["1v6" "2v3" "4v5"]
   ["1v4" "2v6" "3v5"]
   ["1v2" "3v4" "5v6"]])

(def table-6-1-2
  "Table 6.1.2 - Six-team round-robin – version 2:
   The following schedule is played in one day. Games are played to 11."
  [["1v3" "2v5" "4v6"]
   ["1v5" "2v4" "3v6"]
   ["1v2" "3v4" "5v6"]
   ["1v4" "2v6" "3v5"]
   ["1v6" "2v3" "4v5"]])

(def table-6-1-3
  "Table 6.1.2 - Six-team round-robin – version 2:
   The following schedule is played in one day. Games are played to 11."
  [["1v3" "2v5" "4v6"]
   ["1v5" "2v4" "3v6"]
   ["1v2" "3v4" "5v6"]
   ["1v4" "2v6" "3v5"]
   ["1v6" "2v3" "4v5"]])
