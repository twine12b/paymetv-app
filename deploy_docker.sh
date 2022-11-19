
#mvn package && java -jar target/paymetv-0.0.1-SNAPSHOT.jar
#mvn package
#docker build -t paymetv-app:latest .
#docker run -dp 192.168.0.2:80:8080 paymetv-app:latest
#CMD `kube-up.sh`
#kubectl apply -f ./src/main/resources/conf/pmtv-full-config.yaml
kubectl apply -f ./src/main/resources/conf/pmtv-ingress.yaml