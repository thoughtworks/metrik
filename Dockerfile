FROM gradle:jdk11-hotspot as builder
COPY . /app
WORKDIR /app
RUN ls -l; gradle clean build -i

FROM adoptopenjdk:11-jdk-hotspot
COPY --from=builder /app/build/libs/*.jar /app.jar

ENTRYPOINT ["java", "-jar", "/app.jar"]