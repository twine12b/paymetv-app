FROM amazoncorretto:17-alpine AS builder
WORKDIR /app
COPY target/paymetv-app-0.0.1-SNAPSHOT.jar app.jar
RUN java -Djarmode=layertools -jar app.jar extract

FROM amazoncorretto:17-alpine
WORKDIR /app
RUN apk add --no-cache wget
COPY --from=builder /app/dependencies/ ./
COPY --from=builder /app/spring-boot-loader/ ./
COPY --from=builder /app/snapshot-dependencies/ ./
COPY --from=builder /app/application/ ./

LABEL maintainer="PayMeTV Team"
LABEL version="0.0.1-SNAPSHOT"
EXPOSE 80

HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD wget --quiet --tries=1 --spider http://localhost:80/actuator/health || exit 1

ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]

#FROM bellsoft/liberica-openjdk-alpine
#LABEL authors="twine"
#LABEL myimage="keepit"
#
#ARG JAR_FILE=target/*.jar
#COPY ${JAR_FILE} app.jar
#
#EXPOSE 80
#
#ENTRYPOINT ["java","-jar","/app.jar"]