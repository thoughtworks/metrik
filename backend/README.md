# 4-key-metrics backend API

This is the backend API layer of the 4-key-metrics measurement tool. 

# Getting Started

### Tech stack
* java 11
* gradle
* kotlin
* mongo DB  
* springBoot
* junit 5 for testing


### Run locally
Checkout the repo to local and go to the project folder: `${REPO_FOLDER}/backend`
* Build: 
```aidl
./gradlew clean build 
```
* Set up mongodb locally: 
```aidl
cd mongodb-setup/mongodb-for-loca
./setup-mongodb.sh
```
* Start up APP locally
```aidl
go to project folder first: 
cd ../../
then
./gradlew clean bootRun 
```
  Then you can access [http://localhost:9000/actuator/health](http://localhost:9000/actuator/health) to check the app health


### Run Api Tests

#### Feed test data 
For those who first time running api tests on your local, you need to feed test data for api tests into your local mongodb. 
1) run connect-to-mongodb.sh
```aidl
./connect-to-mongodb.sh
```

#### Run Api tests
To run api tests, we need start our backend first.
```aidl
 SPRING_PROFILES_ACTIVE=apitest ./gradlew clean bootRun
```

To run all api tests, run command below
```aidl
./gradlew clean apiTest
```

To run all *.feature files under src.api-test/karate/metrics, run command below
```aidl
./gradlew clean apiTest --tests MetricsRunner
```

To run one single feature file, for example, deployment-frequency.feature
```aidl
./gradlew clean apiTest --tests MetricsRunner -Dkarate.options=classpath:karate/metrics/deployment-frequency.feature
```

To skip a scenario in feature file, add "@skip" tag above scenario.
```aidl
@skip
Scenario: targeted stage status is successful and time is within the selected date range should be counted in
```

To Run api test in one command. Gradle task will start mongodb, seed test data, start bootRun and run api tests.
```aidl
./gradlew --stop && ./gradlew apiTestOneCommand
```


### Swagger documentation
Swagger url: [http://localhost:9000/swagger-ui/index.html](http://localhost:9000/swagger-ui/index.html)




