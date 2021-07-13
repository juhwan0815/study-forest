FROM openjdk:17-ea-11-jdk-slim

VOLUME /tmp
COPY build/libs/study-service-0.0.1-SNAPSHOT.jar studyService.jar

ENTRYPOINT ["java","-jar","studyService.jar"]