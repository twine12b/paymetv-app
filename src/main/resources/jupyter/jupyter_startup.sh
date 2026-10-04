#!/bin/sh
set -eu

SCRIPT_DIR=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)
CONF_DIR="$SCRIPT_DIR/conf"
NAMESPACE="jupyter"
RELEASE="jupyter"

kubectl get namespace ${NAMESPACE} >/dev/null 2>&1 || kubectl create namespace ${NAMESPACE}

helm repo add jupyterhub https://jupyterhub.github.io/helm-chart/ --force-update
helm repo update jupyterhub

kubectl apply -f "$CONF_DIR/namespace.yaml"
helm upgrade --install "$RELEASE" jupyterhub/jupyterhub \
  --namespace "$NAMESPACE" \
  --values "$CONF_DIR/values.yaml" \
  --wait \
  --timeout 10m

printf '\nJupyterHub is ready. Forward the web service with:\n'
printf 'kubectl --namespace %s port-forward service/proxy-public 8000:80\n' "$NAMESPACE"
printf 'Then open http://localhost:8000\n'
