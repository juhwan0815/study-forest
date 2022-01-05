FROM openjdk:17-ea-11-jdk-slim

VOLUME /tmp
COPY build/libs/notification-service-0.0.1-SNAPSHOT.jar notificationService.jar

ENTRYPOINT ["java","-jar","notificationService.jar"]