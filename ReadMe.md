kubectlkubect[![Build Status](http://www.paymetv.co.uk:8080/buildStatus/icon?job=paymetv-pipeline)](http://www.paymetv.co.uk:8080/job/paymetv-pipeline/)
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

------
Get initalAdminPassword
------
docker exec -it <container_id> sh
````


https://github.com/nabsul/k8s-letsencrypt

http://localhost/.well-known/acme-challenge/wqGWVlPkkeuKb_jhlwwQn2bAeYSgeNiLbphv1rzjTp0

https://platform9.com/learn/v1.0/tutorials/nginix-controller-via-yaml

Kubernetes SSL with cert-manager
https://youtu.be/MRhEWpkd5Ig?si=GLmRAPDKjZbyv1Zv
https://github.com/gurlal-1/devops-avenue/tree/main/yt-videos/kind-cert-manager