#!/bin/bash

namespace="default"

kubectl delete deployments -n $namespace --all
kubectl delete svc -n $namespace --all
kubectl delete ingress -n $namespace --all

#kubectl config delete-context kind-certmanager
#kubectl delete ns ${namespace}

# Delete all namespaces except kube-system, kube-public, and default
#for ns in $(kubectl get ns --no-headers | awk '{if ($1 != "kube-system" && $1 != "kube-public" && $1 != "default") print $1}'); do
#  kubectl delete ns $ns
#done

