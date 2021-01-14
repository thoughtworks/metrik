# SEA-4-Key-Metrics-frontend

ThoughtWorks SEA MU beach project - 4 Key Metrics - Frontend

# Requirement
Node Version >= 10

# Get Started
## Tech Stack
* React
* Typescript	
* Webpack
* Jest & Testing-Library For React

## Usage
* Run
```bash
npm start
```

* Build
```bash
npm run build:prod
```
if you want to analyze the built details, you can use:
```bash
npm run build:prod:analyze
```

* Test
```bash
npm test
```

## Caveats
* using git **pre-commit** hook to do type and code style checking, so if there's any type and code style errors. We won't let you commit your changes 
* using git **pre-push hook** to do unit test checking, so if there's any unit test errors, Or you don't meet the test coverage threshold. We won't let you push your commits
