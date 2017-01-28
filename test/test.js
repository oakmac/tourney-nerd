//------------------------------------------------------------------------------
// Require Modules
//------------------------------------------------------------------------------

const assert = require('assert');
const kidif = require('kidif');
const tourneyNerd = require('../tourney-nerd.js');

// TODO:
// - should we prefix all the test case paths using process.cwd() or similar?

//------------------------------------------------------------------------------
// Advance Tournament
//------------------------------------------------------------------------------

// TODO: assert that the tournament states are valid here
function testAdvanceTournamentCase(testCase) {
  var beforeState = JSON.parse(testCase.before);
  var afterState = JSON.parse(testCase.after);
  var advancedState = tourneyNerd.advanceTournament(beforeState);

  it(testCase.description, function() {
    assert.deepStrictEqual(advancedState, afterState);
  });
}

function testAdvanceTournament() {
  var testCases = kidif('test/advance-tournament/*.test');
  testCases.forEach(testAdvanceTournamentCase);
}

describe('Advance Tournament', testAdvanceTournament);
