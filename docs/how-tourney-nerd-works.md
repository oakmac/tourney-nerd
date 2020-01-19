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
  - Self-explanatory: the fields where the games will be played. An event should have
    one set of fields to be used for competitive play.
  - Fields are not restricted to Divisions. ie: Field A may host a game for the Open Division,
    and then later host a game for the Mixed Division.
- **Schedule**
  - A timeline for the event. Each entry in the schedule can be linked to a Game.
- **Teams**
  - Self-explanatory: the teams which will compete in the event.
  - A Team is always part of a Division (ie: one-to-one relationship between Teams and Divisions).
- **Games**
  - Self-explanatory: a game between two teams.
  - Games are always part of a Division (the two teams who play the game must be part of the same Division).
  - Games occur on a field.
  - Games can be linked to an entry on the Schedule.
  - Games are always part of a **Game Group** (see below).
  - Games must always be one of the following statuses:
    - scheduled
    - in progress
    - final (ie: the game was played to completion)
    - aborted (the game was being played, and aborted for some reason like weather or injury)
    - canceled (the game was scheduled to be played, but canceled for some reason)
- **Game Groups**
  - A set of Games for the purpose of defining the tournament's structure.
  - Example: Round-robin Pool A, Winner's Bracket, Swiss Round 3, etc
  - The game groups supported by tourney-nerd are:
    - Round-robin pools
    - Single-elimination bracket
    - Double-elimination bracket
    - Swiss system round
- **Results**
  - Information about team's performance for a set of games:
    - Win / loss
    - Points for / against
    - Place against other teams (including any necessary tie-breaker logic)
  - Results can be calculated for any set of games. For example: you can have results
    from a single round-robin pool, or results for all of the games in a Division.

## Tournament Structure

With the basic building blocks of an event as described above, an event's
structure is controlled by information found in **Games** and **Game Groups**.

- Games can be linked to the result of another game.
  - Example: `teamA` for `game-1142` can be `"winner of game-1089"`
  - Once the status of `game-1089` is `"final"`, tourney-nerd will automatically
    populate the `teamA` value for `game-1142`
- Games can be linked to the result of a Game Group.
  - Example: `teamB` for `game-2441` can be `"3rd place Pool B"` (where `"Pool B"` is a round-robin Game Group)
  - Once all of the games in `"Pool B"` have been played (ie: status set to `"final"`), tourney-nerd will
    calculate the Results for that Pool and populate `teamB` for `game-2441`
