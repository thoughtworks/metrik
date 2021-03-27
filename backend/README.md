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
* Java 11
* Gradle
* Kotlin
* MongoDB
* SpringBoot
* Junit 5 for unit testing

# Getting Started
## Run Locally
Checkout the repo to local and go to the project folder: `${REPO_FOLDER}/backend`
* Build: 
```
./gradlew clean build 
```
* Set up mongodb locally: 
```
cd mongodb-setup/mongodb-for-local
./setup-mongodb.sh
```
* Start up APP locally
```
./gradlew clean bootRun 
```
Then you can access [http://localhost:9000/swagger-ui/index.html](http://localhost:9000/swagger-ui/index.html) to check APIs.


## Run API Tests

### Feed Test Data 
For those who first time running api tests on your local, you need to feed test data for api tests into your local mongodb. 
1) run connect-to-mongodb.sh
```
./connect-to-mongodb.sh
```

### Run API Tests
To run api tests, we need start our backend first.
```
SPRING_PROFILES_ACTIVE=apitest ./gradlew clean bootRun
```

To run all api tests, run command below
```
./gradlew clean apiTest
```

To run all *.feature files under src.api-test/karate/metrics, run command below
```
./gradlew clean apiTest --tests MetricsRunner
```

To run one single feature file, for example, deployment-frequency.feature
```
./gradlew clean apiTest --tests MetricsRunner -Dkarate.options=classpath:karate/metrics/deployment-frequency.feature
```

To skip a scenario in feature file, add "@skip" tag above scenario.
```
@skip
Scenario: targeted stage status is successful and time is within the selected date range should be counted in
```

To Run api test in one command. Gradle task will start mongodb, seed test data, start bootRun and run api tests.
```
./gradlew --stop && ./gradlew apiTestOneCommand
```


## Swagger Documentation
Swagger url: [http://localhost:9000/swagger-ui/index.html](http://localhost:9000/swagger-ui/index.html)


# Configuration

## Detekt
This project use [Detekt](https://github.com/detekt/detekt) for static code analysing, you can adjust rules in `./gradle/detekt/detekt.yml`

## Jacoco
This project use [Jacoco](https://github.com/jacoco/jacoco) for unit test coverage checking.

The configuration file is at `./gradle/jacoco.gradle`. You can exclude unnecessary files from the check, also you can adjust the coverage ratio there, but don't decrease it unless you have a good reason.
