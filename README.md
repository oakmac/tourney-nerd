# Tourney Nerd [![Build Status](https://travis-ci.org/oakmac/tourney-nerd.svg?branch=master)](https://travis-ci.org/oakmac/tourney-nerd)

A library for handling [Ultimate] tournament logic.

## About

> TODO: write more about the purpose of this project

## Development TODO

- tie-breaker rules for round robins
- schedule rules for number of teams
  - ie: 5 teams total, what is the schedule?

## Clojure Development

```sh
# run tests
lein test
```

## JS Development Setup

Install [Leiningen], [Node.js], and [yarn].

```sh
# install node_modules (one-time)
yarn install

# builds the tourney-nerd.js file
lein clean && lein cljsbuild once

# run the test cases
npm test
```

## License

[ISC License]

[Ultimate]:https://en.wikipedia.org/wiki/Ultimate_(sport)
[Leiningen]:http://leiningen.org
[Node.js]:http://nodejs.org
[yarn]:https://yarnpkg.com/
[ISC License]:LICENSE.md
