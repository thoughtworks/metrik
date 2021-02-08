# SEA-4-Key-Metrics-service
This is the backend implementation about [4 key metric](https://cloud.google.com/blog/products/devops-sre/using-the-four-keys-to-measure-your-devops-performance). 

[![CircleCI](https://circleci.com/gh/twlabs/SEA-4-Key-Metrics-service.svg?style=svg&circle-token=7f6ed8d0a1d7cb129e27853b3c38b9bc62cc1575)](https://circleci.com/gh/twlabs/SEA-4-Key-Metrics-service)

# Getting Started

### Tech stack
* java 11
* gradle
* kotlin
* mongo DB  
* springBoot
* junit 5 for testing


### Run locally
Checkout the repo to local and go to the project folder: /**/sea-4-key-metrics-service
* Build: 
```aidl
./gradlew clean build 
```
* Start App: 
```aidl
*  you need to start mongo locally first. 
Please use the script to start up mongo DB locally. Please refer to the link below(please make sure the DB passworkd is 4000km):
https://docs.google.com/spreadsheets/d/1qyyYG4XNPvOlmGSaP8Bs4OrnpBXvKTIkVK5M46R--3Q/edit#gid=350000577
* then run ./gradlew clean bootRun
```
Then you can access [http://localhost:9000/actuator/health](http://localhost:9000/actuator/health) to check the app health


### Run Api Tests

#### Feed test data 
For those who first time running api tests on your local, you need to feed test data for api tests into your local mongodb. 
1) replace "******" in connect-to-mongodb.sh with your local mongodb password
```
docker exec -i $container_id mongo --port 27017 -u "4km" --authenticationDatabase "4-Key-Metrics" -p "******" < ./feed-test-data.js
```
2) run connect-to-mongodb.sh
```
./connect-to-mongodb.sh
```

#### Run Api tests
To run api tests, we need start our backend first.
```
./gradlew clean bootRun
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


### Swagger documentation
Swagger url: [http://localhost:9000/swagger-ui/index.html](http://localhost:9000/swagger-ui/index.html)




