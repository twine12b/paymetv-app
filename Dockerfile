FROM bellsoft/liberica-openjdk-alpine
LABEL authors="twine"
LABEL myimage="keepit"

ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar

EXPOSE 80

ENTRYPOINT ["java","-jar","/app.jar"]