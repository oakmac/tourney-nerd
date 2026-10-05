# Changelog

All notable changes to this project will be documented in this file.

## [Unreleased]

### Added
- [Issue #17] - run the test suite in ClojureScript on Node.js (in addition to the JVM)
- `valid-division?`, `valid-field?`, `valid-game?`, `valid-team?`, `valid-timeslot?` predicates
- `division-id?`, `field-id?`, `game-id?`, `group-id?`, `team-id?`, `timeslot-id?` predicates in
  the `util.ids` namespace
- `results/team->streak` - a team's current win / loss / tie streak across its finished games
- `results/group->placements` - the places decided by a bracket's placement games, including
  the places that are still undecided and the game that will decide them. Works for a bracket
  that is partially played (or not played at all), unlike `group->sorted-results`.
- `get-group-by-id`, `get-field-by-id`, `get-timeslot-by-id` (alongside the existing
  `get-team-by-id`), and `util/get-by-id` underneath them. All accept an Event or map keyed
  by string or keyword, and an id that is a string or keyword.

### Changed
- tourney-nerd now has zero dependencies (removed malli and timbre)
- Clojure 1.11.1 -> 1.12.6
- `games->sorted-results` throws on an unrecognized tiebreaking method instead of
  logging an error and falling back to victory points

### Removed
- `division-schema`, `field-schema`, `game-schema`, `team-schema`, `timeslot-schema`
  (malli schemas); use the `valid-*?` predicates instead

### Fixed
- Woodlands League tiebreaker threw an exception when two teams were tied on
  record, point diff, and points scored and the teams map was keyed by keyword
  (ie: an Event decoded from JSON). Two teams that have not played any games now
  compare as tied.

## [0.14.0] - 2025-11-10

- [PR-16] - support results from bracket game groups

## [0.13.0] - 2025-03-22

- [PR-15] - use tiebreaker method specified on a Group or Event when advancing an event

## [0.12.0] - 2025-03-22

- [PR-14] - added tiebreaker rules for The Woodlands league events

## [0.11.0] - 2024-12-19

### Fixed
- [Issue #12] - Clear pending teams when we clone an event ([commit #a8d80197])

## [0.10.0] - 2023-11-29

- Initial release to clojars

[Unreleased]: https://github.com/oakmac/tourney-nerd/compare/v0.14.0...HEAD
[0.14.0]: https://github.com/oakmac/tourney-nerd/releases/tag/v0.14.0
[0.13.0]: https://github.com/oakmac/tourney-nerd/releases/tag/v0.13.0
[0.12.0]: https://github.com/oakmac/tourney-nerd/releases/tag/v0.12.0
[0.11.0]: https://github.com/oakmac/tourney-nerd/releases/tag/v0.11.0
[0.10.0]: https://github.com/oakmac/tourney-nerd/releases/tag/v0.10.0

[Issue #12]:https://github.com/oakmac/tourney-nerd/issues/12
[Issue #17]:https://github.com/oakmac/tourney-nerd/issues/17

[PR-14]:https://github.com/oakmac/tourney-nerd/pull/14
[PR-15]:https://github.com/oakmac/tourney-nerd/pull/15
[PR-16]:https://github.com/oakmac/tourney-nerd/pull/16

[commit #a8d80197]:https://github.com/oakmac/tourney-nerd/commit/a8d801974c850e82f0c1d987b2b324ef537f9a59
