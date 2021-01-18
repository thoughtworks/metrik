# Project introduction
This is the backend implementation about [4 key metric](https://cloud.google.com/blog/products/devops-sre/using-the-four-keys-to-measure-your-devops-performance).   


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



