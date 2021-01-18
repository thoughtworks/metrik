FROM gradle:jdk11-hotspot as builder
COPY . /app
WORKDIR /app
RUN ls -l; gradle clean build -i

FROM adoptopenjdk:11-jdk-hotspot
COPY --from=builder /app/build/libs/*.jar /app/sea-4-key-metrics-service.jar.jar
ENV APP_ENV dev

EXPOSE 9000

CMD ["/app/run.sh"]