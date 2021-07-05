FROM openjdk:17-ea-11-slim
VOLUME /tmp
COPY build/libs/gateway-service-0.0.1-SNAPSHOT.jar gatewayService.jar
ENTRYPOINT ["java","-jar","gatewayService.jar"]