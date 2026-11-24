FROM hashicorp/terraform:1.17.2 AS terraform

FROM localemu/localemu:latest

ARG TARGETARCH
ARG HELM_VERSION=v4.3.1
ARG KUBECTL_VERSION=v1.31.6
ARG K3D_VERSION=v5.8.3

# LocalEmu provides the AWS APIs. EKS is backed by a k3d cluster created
# through the Docker socket mounted by Compose.
ENV PERSISTENCE=1 \
    EKS_K8S_PROVIDER=k3d \
    DOCKER_HOST=unix:///var/run/docker.sock \
    AWS_ACCESS_KEY_ID=AKIAIOSFODNN7EXAMPLE \
    AWS_SECRET_ACCESS_KEY=wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY \
    AWS_DEFAULT_REGION=us-east-1 \
    AWS_ENDPOINT_URL=http://localhost:4566 \
    TERRAFORM_SCRIPTS_DIR=/home/terraform \
    KUBECONFIG=/root/.kube/config \
    LOCALEMU_KUBE_HOST=host.docker.internal

RUN apt-get update \
    && apt-get install -y --no-install-recommends ca-certificates curl groff less net-tools tar unzip wget \
    && rm -rf /var/lib/apt/lists/*

# Install AWS CLI and Kubernetes tools for the target CPU architecture.
RUN set -eu; \
    case "${TARGETARCH}" in \
      amd64) AWSCLI_ARCH=x86_64; BINARCH=amd64 ;; \
      arm64) AWSCLI_ARCH=aarch64; BINARCH=arm64 ;; \
      *) echo "Unsupported target architecture: ${TARGETARCH}" >&2; exit 1 ;; \
    esac; \
    curl -fsSLo /tmp/awscliv2.zip "https://awscli.amazonaws.com/awscli-exe-linux-${AWSCLI_ARCH}.zip"; \
    unzip -q /tmp/awscliv2.zip -d /tmp; \
    /tmp/aws/install; \
    curl -fsSLo /usr/local/bin/kubectl "https://dl.k8s.io/release/${KUBECTL_VERSION}/bin/linux/${BINARCH}/kubectl"; \
    chmod 0755 /usr/local/bin/kubectl; \
    curl -fsSLo /usr/local/bin/k3d "https://github.com/k3d-io/k3d/releases/download/${K3D_VERSION}/k3d-linux-${BINARCH}"; \
    chmod 0755 /usr/local/bin/k3d; \
    curl -fsSLo /tmp/helm.tar.gz "https://get.helm.sh/helm-${HELM_VERSION}-linux-${BINARCH}.tar.gz"; \
    tar -xzf /tmp/helm.tar.gz -C /tmp; \
    install -m 0755 "/tmp/linux-${BINARCH}/helm" /usr/local/bin/helm; \
    rm -rf /tmp/aws /tmp/awscliv2.zip /tmp/helm.tar.gz "/tmp/linux-${BINARCH}"; \
    kubectl version --client=true; \
    k3d version; \
    helm version --short

COPY --from=terraform /bin/terraform /usr/local/bin/terraform
COPY aws-emulator/terraform/*.tf /home/terraform/
COPY aws-emulator/terraform/.terraform.lock.hcl /home/terraform/.terraform.lock.hcl
COPY aws-emulator/terraform/terraform-initializer.sh /home/terraform/terraform-initializer.sh
COPY aws-emulator/localemu-kubeconfig /usr/local/bin/localemu-kubeconfig

RUN chmod 0755 /usr/local/bin/localemu-kubeconfig \
    && mkdir -p /root/.kube /manifests

WORKDIR /home/terraform

EXPOSE 4566 4510-4559
