FROM gradle:jdk11-hotspot as builder

ENV APP_HOME=/app
WORKDIR $APP_HOME

COPY build.gradle.kts settings.gradle.kts gradlew $APP_HOME
COPY gradle $APP_HOME/gradle

RUN ./gradlew clean build --no-daemon || return 0

COPY . $APP_HOME
RUN ./gradlew clean build --no-daemon


FROM adoptopenjdk:11-jre-hotspot
COPY --from=builder /app/build/libs/*.jar /app/sea-4-key-metrics-service.jar.jar
ENV APP_ENV dev

EXPOSE 9000

CMD ["/app/run.sh"]