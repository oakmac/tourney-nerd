/* eslint-env mocha */

// -----------------------------------------------------------------------------
// Require Modules
// -----------------------------------------------------------------------------

const assert = require('assert')
const kidif = require('kidif')
const tourneyNerd = require('../tourney-nerd.js')

// TODO:
// - should we prefix all the test case paths using process.cwd() or similar?

// -----------------------------------------------------------------------------
// Results
// -----------------------------------------------------------------------------

function testResultCase (testCase) {
  var tournament = JSON.parse(testCase.tournament)
  var teams = tournament.teams
  var games = tournament.games
  var expectedResult = JSON.parse(testCase.results)
  var calculatedResult = tourneyNerd.calculateResults(teams, games)

  it(testCase.description, function() {
    assert.deepStrictEqual(calculatedResult, expectedResult)
  })
}

function testResults () {
  var testCases = kidif('test/results/*.test')
  testCases.forEach(testResultCase)
}

describe('Result Calculations', testResults)

// -----------------------------------------------------------------------------
// Advance Tournament
// -----------------------------------------------------------------------------

// TODO: assert that the tournament states are valid here
function testAdvanceTournamentCase (testCase) {
  var beforeState = JSON.parse(testCase.before)
  var afterState = JSON.parse(testCase.after)
  var advancedState = tourneyNerd.advanceTournament(beforeState)

  it(testCase.description, function () {
    assert.deepStrictEqual(advancedState, afterState)
  })
}

function testAdvanceTournament () {
  var testCases = kidif('test/advance-tournament/*.test')
  testCases.forEach(testAdvanceTournamentCase)
}

describe('Advance Tournament', testAdvanceTournament)
