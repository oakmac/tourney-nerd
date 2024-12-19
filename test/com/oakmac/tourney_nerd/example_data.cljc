(ns com.oakmac.tourney-nerd.example-data)

(def ex-division-id "division-1000")
(def ex-game-group-id "group-1000")

(def four-teams
  {"team-1000" {:name "Team 1"
                :order 1
                :id "team-1000"
                :division-id "division-1000"}
   "team-1001" {:name "Team 2"
                :order 2
                :id "team-1001"
                :division-id "division-1000"}
   "team-1002" {:name "Team 3"
                :order 3
                :id "team-1002"
                :division-id "division-1000"}
   "team-1003" {:name "Team 4"
                :order 4
                :id "team-1003"
                :division-id "division-1000"}})

(def eight-teams
  {"team-1000" {:name "Team 1"
                :order 1
                :id "team-1000"
                :division-id "division-1000"}
   "team-1001" {:name "Team 2"
                :order 2
                :id "team-1001"
                :division-id "division-1000"}
   "team-1002" {:name "Team 3"
                :order 3
                :id "team-1002"
                :division-id "division-1000"}
   "team-1003" {:name "Team 4"
                :order 4
                :id "team-1003"
                :division-id "division-1000"}
   "team-1004" {:name "Team 5"
                :order 5
                :id "team-1004"
                :division-id "division-1000"}
   "team-1005" {:name "Team 6"
                :order 6
                :id "team-1005"
                :division-id "division-1000"}
   "team-1006" {:name "Team 7"
                :order 7
                :id "team-1006"
                :division-id "division-1000"}
   "team-1007" {:name "Team 8"
                :order 8
                :id "team-1007"
                :division-id "division-1000"}})

(def four-fields
  {"field-1000" {:name "Field 1"
                 :order 1
                 :id "field-1000"
                 :description ""}
   "field-1001" {:name "Field 2"
                 :order 2
                 :description ""
                 :id "field-1001"}
   "field-1002" {:name "Field 3"
                 :order 3
                 :description ""
                 :id "field-1002"}
   "field-1003" {:name "Field 4"
                 :order 4
                 :description ""
                 :id "field-1003"}})

(def weekend-tournament-schedule
  {"timeslot-1000" {:name "Bagels and Bananas"
                    :time "2020-02-13 09:00"
                    :id "timeslot-1000"}
   "timeslot-1001" {:name "Pool Play Round 1"
                    :time "2020-02-13 09:30"
                    :id "timeslot-1001"}
   "timeslot-1002" {:name "Pool Play Round 2"
                    :time "2020-02-13 10:00"
                    :id "timeslot-1002"}
   "timeslot-1003" {:name "Pool Play Round 3"
                    :time "2020-02-13 11:30"
                    :id "timeslot-1003"}
   "timeslot-1004" {:name "Pool Play Round 4"
                    :time "2020-02-13 11:30"
                    :id "timeslot-1004"}
   "timeslot-1005" {:name "Break for lunch"
                    :time "2020-02-13 11:30"
                    :id "timeslot-1005"}
   "timeslot-1006" {:name "Pool Play Round 5"
                    :time "2020-02-13 14:30"
                    :id "timeslot-1006"}
   "timeslot-1007" {:name "Pool Play Round 6"
                    :time "2020-02-13 16:00"
                    :id "timeslot-1007"}
   "timeslot-1008" {:name "Saturday Night Party!"
                    :time "2020-02-13 19:00"
                    :id "timeslot-1008"}

   "timeslot-2000" {:name "Bagels and Bananas"
                    :time "2020-02-14 09:00"
                    :id "timeslot-2000"}
   "timeslot-2001" {:name "Quarter-finals"
                    :time "2020-02-14 09:30"
                    :id "timeslot-2001"}
   "timeslot-2002" {:name "Semi-finals"
                    :time "2020-02-14 11:00"
                    :id "timeslot-2002"}
   "timeslot-2003" {:name "Finals"
                    :time "2020-02-14 14:00"
                    :id "timeslot-2003"}})
