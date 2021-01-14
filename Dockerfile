FROM openjdk:11

ENV APP_ENV dev

RUN ./gradlew clean build

COPY ./build/libs/*.jar /app/sea-4-key-metrics-service.jar
COPY ./run.sh /app/

EXPOSE 9000

CMD ["/app/run.sh"]
