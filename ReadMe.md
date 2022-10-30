##Paymetv limited
###The fast track way to get paid for your videos


####Deploy/Setup
````
mvn package && java -jar target/paymetv-0.0.1-SNAPSHOT.jar
docker build -t paymetv-app:latest .
docker run -dp 192.168.0.2:80:8080 paymetv-app:latest
````
----
````
docker run -p 8080:8080 -p 50000:50000 -d -v jenkins_home:/var/jenkins_home jenkins/jenkins:lts
usr: jenkins
pwd: jenkins1
````