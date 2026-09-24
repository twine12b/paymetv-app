bash id="4vosi"

kubectl get namespace testing >/dev/null 2>&1 || kubectl create namespace testing

kubectl run test-client \
-n kafka \
--rm -it \
--image=curlimages/curl:8.4.0 \
--restart=Never -- /bin/sh