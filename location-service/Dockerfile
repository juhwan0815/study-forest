FROM openjdk:17-ea-11-jdk-slim

VOLUME /tmp
COPY build/libs/location-service-0.0.1-SNAPSHOT.jar locationService.jar

ENTRYPOINT ["java","-jar","locationService.jar"]