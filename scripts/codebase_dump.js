#! /usr/bin/env node

// Dump the essential codebase files into a single text file for LLM consumption

const fs = require('fs')

const filesToDump = [
  'README.md',
  'docs/how-tourney-nerd-works.md',
  'src-cljc/com/oakmac/tourney_nerd/advance_event.cljc',
  'src-cljc/com/oakmac/tourney_nerd/constants.cljc',
  'src-cljc/com/oakmac/tourney_nerd/divisions.cljc',
  'src-cljc/com/oakmac/tourney_nerd/events.cljc',
  'src-cljc/com/oakmac/tourney_nerd/games.cljc',
  'src-cljc/com/oakmac/tourney_nerd/groups.cljc',
  'src-cljc/com/oakmac/tourney_nerd/order.cljc',
  'src-cljc/com/oakmac/tourney_nerd/results.cljc',
  'src-cljc/com/oakmac/tourney_nerd/schedule.cljc',
  'src-cljc/com/oakmac/tourney_nerd/teams.cljc',

  'test-resources/2025-woodlands-fall-league.json',
  'test-resources/2025-woodlands-spring-league.after.json',
  'test-resources/2025-woodlands-spring-league.before.json',
  'test-resources/2025-woodlands-spring-league.json',
  'test-resources/woodlands-fall-league.after.json',
  'test-resources/woodlands-fall-league.before.json',

  'test/com/oakmac/tourney_nerd/advance_event_test.cljc',
  'test/com/oakmac/tourney_nerd/events_test.cljc',
  'test/com/oakmac/tourney_nerd/results_test.cljc'
]

let outTxt = ''

filesToDump.forEach((f) => {
  const fileContents = fs.readFileSync(f, 'utf8')

  outTxt = outTxt + '\n<<<<<<<<<< START FILE: ' + f + ' >>>>>>>>>>\n\n'
  outTxt = outTxt + fileContents + '\n'
  outTxt = outTxt + '<<<<<<<<<< END FILE: ' + f + ' >>>>>>>>>>\n'

  console.log('Dumping:', f)
})

fs.writeFileSync('code_dump.txt', outTxt)
