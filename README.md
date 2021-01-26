# SEA-4-Key-Metrics-service
This is the backend implementation about [4 key metric](https://cloud.google.com/blog/products/devops-sre/using-the-four-keys-to-measure-your-devops-performance). 

[![CircleCI](https://circleci.com/gh/twlabs/SEA-4-Key-Metrics-service.svg?style=svg&circle-token=7f6ed8d0a1d7cb129e27853b3c38b9bc62cc1575)](https://circleci.com/gh/twlabs/SEA-4-Key-Metrics-service)

# Getting Started

### Tech stack
* java 11
* gradle
* kotlin
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
./gradlew clean bootRun
```
Then you can access [http://localhost:9000/actuator/health](http://localhost:9000/actuator/health) to check the app health

### Swagger documentation
Swagger url: [http://localhost:9000/swagger-ui/index.html](http://localhost:9000/swagger-ui/index.html)




