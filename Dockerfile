FROM openjdk:17-jdk-slim

EXPOSE 8080
EXPOSE 8081
COPY build/libs/*SNAPSHOT.jar /opt/app.jar
WORKDIR /opt
ENTRYPOINT ["java", "-jar", "app.jar"]
