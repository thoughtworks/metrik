# Config git hook in local

## pre-push

Run `./gradlew clean build` before committing code 

```shell
cd SEA-4-Key-Metrics-service
cp config/git-hook/pre-push .git/hooks
```