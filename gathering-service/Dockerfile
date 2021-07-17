FROM openjdk:17-ea-11-jdk-slim

VOLUME /tmp
COPY build/libs/gathering-service-0.0.1-SNAPSHOT.jar gatheringService.jar

ENTRYPOINT ["java","-jar","gatheringService.jar"]