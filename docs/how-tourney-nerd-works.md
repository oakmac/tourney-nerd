# How tourney-nerd Works

## About

[tourney-nerd] is an open source library to help manage the tournament logic for
[Ultimate] tournaments and leagues.

It is not an application designed to be used directly, but rather as "the
brains" behind something more user-friendly.

In theory, tourney-nerd could be used to help run tournaments for many different
sports. It may be extended in this way in the future, but for now the scope will
focus on Ultimate events.

[tourney-nerd]:https://github.com/oakmac/tourney-nerd
[Ultimate]:https://en.wikipedia.org/wiki/Ultimate_(sport)

## Basics

The following are the essential building blocks of tourney-nerd, designed to be
flexible and work with many different tournament structures / formats.

- **Event**
  - A single Ultimate event where games are played competitively.
  - Generally a tournament or a multi-week league.
- **Divisions**
  - A distinct competitive group within a single event. For example: Open, Women, Mixed, Youth, etc.
  - Teams from different divisions do not play against each other competitively in the course of an event.
  - An event may consist of one or more Divisions (but it must have at least one Division, otherwise what is the point ;)).
- **Fields**
  - The fields where the games will be played.
  - An event should have one set of fields to be used for competitive play.
  - Fields are not restricted to Divisions.
    - ie: `Field 1` may host a game for the Open Division, and then later host a game for the Mixed Division.
- **Schedule**
  - A timeline for the event. Consists of Timeslots (see next).
- **Timeslot**
  - A single entry on the Schedule. Games must be linked to a Timeslot in order to
    ensure that Teams and Fields do not get double-booked.
- **Teams**
  - The teams which will compete in the event.
  - A Team is always part of a Division (ie: one-to-one relationship between Teams and Divisions).
- **Games**
  - A game between two Teams. `teamA` and `teamB`
  - Games are always part of a Division (ie: the two teams who play the game must be part of the same Division).
  - Games occur on a field.
  - Games must be linked to a Timeslot on the Schedule.
  - Games are always part of a **Game Group** (see below).
  - Games must be one of the following statuses:
    - `"STATUS_SCHEDULED"` - The game is scheduled to be played.
    - `"STATUS_IN_PROGRESS"` - The game is in progress.
    - `"STATUS_FINAL"` - The game was played to completion.
    - `"STATUS_ABORTED"`- The game was in progress and was aborted for reasons like weather or injury.
    - `"STATUS_CANCELED"` - The game was scheduled to be played, but canceled for some reason.
    - `"STATUS_FORFEIT"` - The game was not played because it was forfeited by one of the teams.
      - TODO: we need to indicate which team forfeited here somehow
- **Game Groups**
  - A set of Games for the purpose of defining the tournament's structure.
  - Example: `Round-robin Pool A`, `Winner's Bracket`, `Swiss Round 3`, etc
  - The game groups supported by tourney-nerd are:
    - Round-robin pools
    - Brackets (single-elimination, double-elimination, etc)
    - Swiss system round
- **Results**
  - Information about teams performance for a set of games:
    - Number of games won, number of games lost (ie: team record)
    - Points for / against / diff
    - Place against other teams (including any necessary tie-breaker logic)
  - Results can be calculated for any set of games.
    - For example: you can have results from a single round-robin pool, or results
      for all of the games in a single Division.

## Tournament Format

With the basic building blocks of an event as described above, an event's format
is controlled by information found in **Games** and **Game Groups**.

- Games can be linked to the result of another game.
  - Example: `teamA` for `game-1142` can be `"winner of game-1089"`
  - Once the status of `game-1089` is `"STATUS_FINAL"`, tourney-nerd will automatically
    populate the `teamA` value for `game-1142` based on the result of `game-1089`
  - This flexible linked list structure supports any form of bracket and "brackets linked
    to other brackets" format (single elimination, double elimination, consolidation, etc)
- Games can also be linked to the result of a Game Group.
  - Example: `teamB` for `game-2441` can be `"3rd place Pool B"` (where `"Pool B"` is a round-robin Game Group)
  - Once all of the games in `"Pool B"` have been played (ie: status set to `"STATUS_FINAL"`), tourney-nerd will
    calculate the Results for that Pool and populate `teamB` for `game-2441`
  - This creates the ability to use different tournament formats within the same event
    where the result of one format seeds the next format. Commonly for Ultimate, this
    is "pool play followed by bracket play". It also allows "swiss round to swiss round"
    progression and "swiss round to bracket play" (as used by [Windmill Windup]).

Since many events share a similar overall format, tourney-nerd allows for
cloning an event game structure such that in most cases a tournament director
will not need to set up "games linked to games" and "games linked to Game
Groups" manually.

Over time, tourney-nerd will grow a corpus of trusted event formats that may be
cloned for any type of event. Including those formats detailed in [The UPA Manual
of Championship Series Tournament Formats].

[Windmill Windup]:https://fixme.com
[The UPA Manual of Championship Series Tournament Formats]:fixme.pdf
