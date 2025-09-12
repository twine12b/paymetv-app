#FROM openjdk:17-jdk-alpine
FROM openjdk:latest
LABEL authors="twine"
LABEL myimage="keepit"

ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar

EXPOSE 80

ENTRYPOINT ["java","-jar","/app.jar"]