# LocalEmu with Kubernetes

LocalEmu runs in Docker and creates a k3d-backed Kubernetes cluster when Terraform provisions the EKS resources. Run Terraform and kubectl on your Mac; their endpoints use `localhost:4566` and the cluster kubeconfig is downloaded from LocalEmu.

## Prerequisites on macOS

Install Docker Desktop, Terraform, AWS CLI, `kubectl`, and `curl`.

## Start LocalEmu and provision the cluster

From this directory:

```sh
docker compose up -d --build
./terraform/terraform-initializer.sh
```

Terraform state is stored in `terraform/terraform.tfstate` on your Mac. Keep that file safe; it records the resources Terraform manages. The initializer uses normal local state locking.

## Apply manifests from your Mac

The initializer saves a host-usable kubeconfig at `~/.kube/localemu-paymetv-eks` and waits for the Kubernetes API. Use it with kubectl:

```sh
KUBECONFIG=~/.kube/localemu-paymetv-eks kubectl get nodes
KUBECONFIG=~/.kube/localemu-paymetv-eks kubectl apply -f manifests/hello-world.yaml
```
```shell
mkdir -p "$HOME/.kube"
curl -fsS http://localhost:4566/_localemu_eks/paymetv-eks/kubeconfig \
  -o "$HOME/.kube/localemu-paymetv-eks"

export KUBECONFIG="$HOME/.kube/localemu-paymetv-eks"
kubectl get nodes
```

Put additional YAML files in `manifests/` and apply them from the Mac with `kubectl apply -f manifests/`.
