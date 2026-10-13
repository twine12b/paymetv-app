# ML training service

This directory builds a separate HTTP service around the Python utilities in `code/`. It accepts authenticated requests, runs an allow-listed script, and saves logs and training models to the persistent volume.

Run the Docker build commands and Kubernetes commands below from this `src/main/resources/ml` directory.

## Images and accelerators

Build and push the CPU image for both common Kubernetes Linux architectures:

```sh
docker buildx build --platform linux/amd64,linux/arm64 --target cpu \
  -t paymetv/ml-trainer:cpu --push .
```

The CPU image works on x86-64 and ARM64 nodes, including Kubernetes running on Apple Silicon. Linux containers on Apple Silicon use the CPU; PyTorch's Apple MPS backend requires macOS and is not exposed to a normal Kubernetes Linux pod.

For NVIDIA nodes, build the CUDA image (currently x86-64):

```sh
docker buildx build --platform linux/amd64 --target cuda \
  -t paymetv/ml-trainer:cuda --push .
```

The training script already selects CUDA when `torch.cuda.is_available()` is true, otherwise it selects MPS when available, then CPU. Kubernetes must have the NVIDIA device plugin/runtime and an available GPU. Apply `deployment.cuda.yaml` **instead of** `deployment.yaml` to request one NVIDIA GPU per replica. Each of the three possible replicas needs a GPU to schedule at the maximum. The CPU deployment is the default and works without GPU hardware.

The Kubernetes scheduler cannot make a GPU resource request optional on an individual pod. Select the CUDA deployment when the cluster has NVIDIA GPU nodes; use the CPU deployment otherwise. CUDA is packaged in the CUDA image and is not installed into a running CPU image.

## Persistent data

The PVC mounts at `/data`. Put each COCO training dataset in `/data/datasets/<dataset-name>` and its annotation JSON and images below that directory. Models and logs are written to `/data/models/<job-id>`.

The PVC requests 20 GiB with `ReadWriteOnce`, which works with the local-path `standard` StorageClass used by the current desktop cluster. That storage class does not support `ReadWriteMany`. The PVC is node-local, so replicas can mount it only from the same node; for multi-node scaling, use an RWX-capable storage class or per-replica claims with an appropriate workload design.

## Install

The ML startup script builds the selected image and applies the ML namespace, PVC, secret, Deployment, Service, and NetworkPolicy. KEDA and HTTP routing/scaling resources are installed separately by `../keda/keda_startup.sh`. Its `InterceptorRoute` records in-flight requests; its `ScaledObject` starts at one replica, targets two concurrent requests per replica, and caps the deployment at three. `ml_startup.sh` builds and deploys the CPU image by default. Set `ML_IMAGE_FLAVOR=cuda` to build and deploy the CUDA profile instead. `paymetv_start_all.sh` runs the KEDA startup after ML and passes its `prod`, `dev`, or `local` environment for the ingress certificate.

```sh
./ml_startup.sh
../keda/keda_startup.sh local
```

The KEDA startup script applies the ingress at `https://paymetv.co.uk/ml/...` through the KEDA interceptor and creates the `paymetv.co.uk` certificate in the `keda` namespace using the matching cluster issuer. The ML startup script creates the `ml-api-auth` Kubernetes Secret with a random bearer token if one does not already exist. Retrieve its base64-encoded value with:

```sh
kubectl -n ml get secret ml-api-auth -o jsonpath='{.data.token}'
```

Decode that value before sending it as an `Authorization: Bearer` header. Requests through `https://paymetv.co.uk/ml/...` pass through the KEDA interceptor and contribute to scaling. Calling the backend `ml-trainer` Service directly bypasses request tracking. In-cluster clients can use `ml-trainer-proxy.ml.svc.cluster.local:8080`.

## Submit a training request

The `/train` endpoint waits for training to finish and returns the job's output paths. The `dataset` value is a directory name under `/data/datasets`.

```sh
curl -X POST https://paymetv.co.uk/ml/train \
  -H "Authorization: Bearer ${ML_API_TOKEN}" \
  -H 'Content-Type: application/json' \
  -d '{"dataset":"my-dataset","epochs":10,"batch_size":1}'
```

Training requests run one at a time per pod by default. Additional in-flight requests wait in the pod and count toward HTTP concurrency scaling. Separate requests write to unique output directories, so their model files do not overwrite one another.

## Run image and dataset scripts

`GET /ml/scripts` returns the enabled script names. `POST /ml/scripts/<script-name>` runs one of the allow-listed image/dataset utilities; the JSON `args` array is passed directly to that script without a shell. Script arguments and outputs use `/data`, which is the PVC mount. A bearer token is required for script and training requests.

For example, to resize an image under `/data/datasets/photos`:

```sh
curl -X POST https://paymetv.co.uk/ml/scripts/image_resizer.py \
  -H "Authorization: Bearer ${ML_API_TOKEN}" \
  -H 'Content-Type: application/json' \
  -d '{"args":["--location","/data/datasets/photos","--file","photo.jpg"]}'
```

The NetworkPolicy allows inbound traffic only from pods in the `keda` namespace. Egress permits cluster DNS and outbound HTTP/HTTPS for operations such as downloading model assets. The cluster CNI must enforce Kubernetes NetworkPolicy resources.
