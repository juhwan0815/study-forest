FROM openjdk:17-ea-11-jdk-slim

VOLUME /tmp
COPY build/libs/auth-service-0.0.1-SNAPSHOT.jar authService.jar

ENTRYPOINT ["java","-jar","authService.jar"]