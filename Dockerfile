from debian:buster-slim

ENV DEBIAN_FRONTEND=noninteractive

RUN mkdir -p /usr/share/man/man1 /usr/share/man/man2

RUN apt-get update && \
apt-get install -y --no-install-recommends \
        openjdk-11-jre

# Prints installed java version, just for checking
RUN java --version

ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar

EXPOSE 80

#RUN curl -fsSLO https://get.docker.com/builds/Linux/x86_64/docker-17.04.0-ce.tgz \
#  && tar xzvf docker-17.04.0-ce.tgz \
#  && mv docker/docker /usr/local/bin \
#  && rm -r docker docker-17.04.0-ce.tgz

ENTRYPOINT ["java","-jar","/app.jar"]