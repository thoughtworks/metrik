[![Backend test](https://github.com/thoughtworks/metrik/actions/workflows/backend_test.yaml/badge.svg)](https://github.com/thoughtworks/metrik/actions/workflows/backend_test.yaml)


<!-- TABLE OF CONTENTS -->
<details open="open">
  <summary>Table of Contents</summary>
  <ul>
    <li><a href="#tech-stack">Tech Stack</a></li>
    <li><a href="#getting-started">Getting Started</a></li>
    <li><a href="#configuration">Configuration</a></li>
  </ul>
</details>
<!-- END OF PROJECT TITLE -->

This is the backend API layer of *Metrik*, the 4-key-metrics measurement tool.

# Tech Stack
* Kotlin
* Java 11
* Gradle
* SpringBoot
* MongoDB
* Junit5, RestAssured

# Getting Started
## Run the API locally
Checkout the repo to local and go to the project folder: `${REPO_FOLDER}/backend`
* Provision a mongodb instance.  
  You can run the following script to start a docker container locally 
```
cd mongodb-setup/mongodb-for-local
./setup-mongodb.sh
```
* Start up locally
```
./gradlew clean bootRun 
```
Then you can start exploring the APIs from the Swagger Doc [http://localhost:9000/swagger-ui/index.html](http://localhost:9000/swagger-ui/index.html).

## Test and Build
* Test and build   
```
./gradlew clean build 
```
*️This will run all unit tests, integration tests, coverage checks, and lint checks. [EmbeddedMongoDB](https://github.com/flapdoodle-oss/de.flapdoodle.embed.mongo) is used here so that you don't need to start a MongoDB instance to run the test suites.*  
* Or, some frequently used tasks
```
# Run unit test
./gradlew clean test

# Run API integration test
./gradlew clean apiTest

# Run one test file to get quicker response
./gradlew apiTest --tests "metrik.CFRCalculationApiTest"

# Detekt code quality analysis
./gradlew detekt
```

# Configuration
## Detekt
This project use [Detekt](https://github.com/detekt/detekt) for static code analysing, configuration file stays in `./gradle/detekt/detekt.yml`

## Jacoco
This project use [Jacoco](https://github.com/jacoco/jacoco) for unit test coverage checking.

The configuration file is at `./gradle/jacoco.gradle`. You can exclude unnecessary files from the check, also you can adjust the coverage ratio there, but don't decrease it unless you have a good reason.
