<!-- TABLE OF CONTENTS -->
<details open="open">
  <summary>Table of Contents</summary>
  <ul>
    <li><a href="#tech-stack">Tech Stack</a></li>
    <li><a href="#requirement">Requirement</a></li>
    <li><a href="#getting-started">Getting Started</a></li>
    <li><a href="#git-hooks">Git Hooks</a></li>
  </ul>
</details>
<!-- END OF PROJECT TITLE -->

This is the frontend SPA layer of *Metrik*, the 4-key-metrics measurement tool.

# Tech Stack
* React
* Typescript
* Recharts
* Webpack
* Jest & Testing-Library For React

# Requirement
Node Version >= 10


# Getting Started
## Run Locally
Checkout the repo to local and go to the project folder: `${REPO_FOLDER}/frontend`

* Install
```bash
npm i
```

* Start up app locally 
```bash
npm run start:local-api
```
Then you can access http://localhost:2333 to check web page.

## Run tests
* Test
```bash
npm test
```
Then coverage folder is built in `${REPO_FOLDER}/frontend`, which includes all the test coverage reports.

## Build bundles
* Build
```bash
npm run build:prod
```

* Build with Analyzer
```bash
npm run build:prod:analyze
```
Then dist folder is built in `${REPO_FOLDER}/frontend`, which includes all the bundles.


# Git Hooks
* use **pre-commit** to do type and code style checking 
* use **pre-push** to do unit test and test coverage checking

