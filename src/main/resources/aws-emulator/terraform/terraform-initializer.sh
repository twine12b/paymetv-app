#!/bin/sh
set -eu

export AWS_ACCESS_KEY_ID="AKIAIOSFODNN7EXAMPLE"
export AWS_SECRET_ACCESS_KEY="wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY"
export AWS_DEFAULT_REGION="us-east-1"
export KUBECONFIG="$HOME/.kube/localemu-paymetv-eks"

# The script runs inside LocalEmu; allow an endpoint override when needed.
LOCAL_EMU_ENDPOINT=${LOCAL_EMU_ENDPOINT:-http://localhost:4566}

TERRAFORM_SCRIPTS_DIR="$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)"
cd "$TERRAFORM_SCRIPTS_DIR"

# Use the project-local state file so Terraform and its lock run on the Mac.
terraform init -input=false -reconfigure -upgrade

state_has() {
  terraform state list | grep -Fqx "$1"
}

import_if_unmanaged() {
  address=$1
  object_id=$2
  if [ -n "$object_id" ] && [ "$object_id" != "None" ] && ! state_has "$address"; then
    terraform import "$address" "$object_id"
  fi
}

# Reconcile resources that may have survived while Terraform's old container
# state was lost. Terraform will create resources that are not found here.
vpc_id=$(aws ec2 describe-vpcs \
  --filters Name=tag:Name,Values=paymetv-eks-vpc \
  --query 'Vpcs[0].VpcId' --output text --endpoint-url "${LOCAL_EMU_ENDPOINT}")
import_if_unmanaged aws_vpc.eks "$vpc_id"

for index in 0 1; do
  subnet_number=$((index + 1))
  subnet_id=$(aws ec2 describe-subnets \
    --filters "Name=tag:Name,Values=paymetv-eks-subnet-${subnet_number}" \
    --query 'Subnets[0].SubnetId' --output text --endpoint-url "${LOCAL_EMU_ENDPOINT}")
  import_if_unmanaged "aws_subnet.eks[$index]" "$subnet_id"
done

if aws iam get-role --role-name paymetv-eks-cluster-role \
     --endpoint-url "${LOCAL_EMU_ENDPOINT}" >/dev/null 2>&1; then
  import_if_unmanaged aws_iam_role.eks_cluster paymetv-eks-cluster-role
  attachment_id=$(aws iam list-attached-role-policies \
    --role-name paymetv-eks-cluster-role \
    --query "AttachedPolicies[?PolicyArn=='arn:aws:iam::aws:policy/AmazonEKSClusterPolicy'].PolicyArn | [0]" \
    --output text --endpoint-url "${LOCAL_EMU_ENDPOINT}")
  if [ -n "$attachment_id" ] && [ "$attachment_id" != "None" ]; then
    import_if_unmanaged aws_iam_role_policy_attachment.eks_cluster \
      "paymetv-eks-cluster-role/${attachment_id}"
  fi
fi

if aws s3api head-bucket --bucket pmtv-ml-dev-bucket12313 \
     --endpoint-url "${LOCAL_EMU_ENDPOINT}" >/dev/null 2>&1; then
  import_if_unmanaged aws_s3_bucket.b pmtv-ml-dev-bucket12313
  import_if_unmanaged aws_s3_bucket_versioning.b pmtv-ml-dev-bucket12313
  import_if_unmanaged aws_s3_bucket_acl.b pmtv-ml-dev-bucket12313
fi

if aws s3api head-bucket --bucket pmtv-admin-dev-bucket12314 \
     --endpoint-url "${LOCAL_EMU_ENDPOINT}" >/dev/null 2>&1; then
  import_if_unmanaged aws_s3_bucket.a pmtv-admin-dev-bucket12314
  import_if_unmanaged aws_s3_bucket_versioning.a pmtv-admin-dev-bucket12314
  import_if_unmanaged aws_s3_bucket_acl.a pmtv-admin-dev-bucket12314
fi

if cluster_id=$(aws eks describe-cluster --name paymetv-eks \
     --query 'cluster.name' --output text --endpoint-url "${LOCAL_EMU_ENDPOINT}" 2>/dev/null); then
  import_if_unmanaged aws_eks_cluster.paymetv "$cluster_id"
elif state_has aws_eks_cluster.paymetv; then
  echo "LocalEmu has no paymetv-eks cluster; removing the stale Terraform state entry so the plan can recreate it." >&2
  terraform state rm -lock-timeout=60s aws_eks_cluster.paymetv
fi

terraform plan -out=tfplan
terraform apply -auto-approve tfplan

# Fetch a host-usable kubeconfig from LocalEmu after creating or adopting the cluster.
export KUBECONFIG="${KUBECONFIG:-$HOME/.kube/localemu-paymetv-eks}"
mkdir -p "$(dirname "$KUBECONFIG")"
curl -fsS "${LOCAL_EMU_ENDPOINT}/_localemu_eks/paymetv-eks/kubeconfig" -o "$KUBECONFIG"

attempt=0
until kubectl get namespaces >/dev/null 2>&1; do
  attempt=$((attempt + 1))
  if [ "$attempt" -ge 60 ]; then
    echo "Kubernetes API did not become ready within 120 seconds." >&2
    kubectl get namespaces
    exit 1
  fi
  sleep 2
done

kubectl get namespaces
