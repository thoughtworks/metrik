FROM openjdk:11

ENV APP_ENV dev

WORKDIR /usr/src/app/temp

COPY . .

RUN ./gradlew clean build

COPY ./build/libs/*.jar /app/sea-4-key-metrics-service.jar
COPY ./run.sh /app/

RUN rm -rf ./*

EXPOSE 9000

CMD ["/app/run.sh"]
