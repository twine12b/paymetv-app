import asyncio
import os
import re
import secrets
import uuid
from pathlib import Path

from fastapi import Depends, FastAPI, HTTPException
from fastapi.security import HTTPAuthorizationCredentials, HTTPBearer
from pydantic import BaseModel, Field


DATASETS_DIR = Path(os.getenv("ML_DATASETS_DIR", "/data/datasets")).resolve()
MODELS_DIR = Path(os.getenv("ML_MODELS_DIR", "/data/models")).resolve()
TRAIN_SCRIPT = Path(os.getenv("ML_TRAIN_SCRIPT", "/opt/ml/code/train.py"))
SCRIPT_ROOT = Path(os.getenv("ML_SCRIPT_ROOT", "/opt/ml/code")).resolve()
TRAINING_CONCURRENCY = max(1, int(os.getenv("TRAINING_CONCURRENCY", "1")))
DATASET_NAME = re.compile(r"^[A-Za-z0-9][A-Za-z0-9._-]{0,127}$")
API_TOKEN = os.getenv("ML_API_TOKEN", "")
RUNNABLE_SCRIPTS = {
    "background_removal_tool.py": "background_removal_tool.py",
    "image_masking_tool.py": "image_masking_tool.py",
    "image_resizer.py": "image_resizer.py",
    "image_composition.py": "cocosynth/image_composition.py",
    "coco_json_utils.py": "cocosynth/coco_json_utils.py",
}
training_slots = asyncio.Semaphore(TRAINING_CONCURRENCY)
bearer = HTTPBearer(auto_error=False)

app = FastAPI(title="PayMeTV ML Trainer", version="1.0.0")


class TrainingRequest(BaseModel):
    dataset: str = Field(description="Folder name under /data/datasets")
    epochs: int = Field(default=10, ge=1, le=1000)
    batch_size: int = Field(default=1, ge=1, le=64)
    accumulation_steps: int = Field(default=1, ge=1, le=128)
    max_image_size: int = Field(default=640, ge=64, le=4096)
    learning_rate: float = Field(default=0.005, gt=0, le=1)


class ScriptRequest(BaseModel):
    args: list[str] = Field(default_factory=list, max_length=64)


@app.get("/healthz")
async def healthz():
    return {"status": "ok"}


def require_api_token(
    credentials: HTTPAuthorizationCredentials | None = Depends(bearer),
):
    if (
        not API_TOKEN
        or credentials is None
        or credentials.scheme.lower() != "bearer"
        or not secrets.compare_digest(credentials.credentials, API_TOKEN)
    ):
        raise HTTPException(
            status_code=401,
            detail="A valid bearer token is required",
            headers={"WWW-Authenticate": "Bearer"},
        )


@app.get("/scripts", dependencies=[Depends(require_api_token)])
async def list_scripts():
    return {"scripts": sorted(RUNNABLE_SCRIPTS)}


@app.post("/scripts/{script_name}", dependencies=[Depends(require_api_token)])
async def run_script(script_name: str, request: ScriptRequest):
    relative_path = RUNNABLE_SCRIPTS.get(script_name)
    if relative_path is None:
        raise HTTPException(status_code=404, detail="Script is not enabled")
    if any("\x00" in argument for argument in request.args):
        raise HTTPException(status_code=400, detail="Arguments cannot contain NUL bytes")

    script_path = (SCRIPT_ROOT / relative_path).resolve()
    if not script_path.is_relative_to(SCRIPT_ROOT) or not script_path.is_file():
        raise HTTPException(status_code=404, detail="Script file is unavailable")

    job_id = str(uuid.uuid4())
    output_dir = MODELS_DIR / job_id
    output_dir.mkdir(parents=True, exist_ok=False)
    log_path = output_dir / "script.log"
    command = ["python3", str(script_path), *request.args]

    async with training_slots:
        try:
            with log_path.open("wb") as log_file:
                process = await asyncio.create_subprocess_exec(
                    *command,
                    cwd="/data",
                    stdout=log_file,
                    stderr=asyncio.subprocess.STDOUT,
                )
                return_code = await process.wait()
        except OSError as exc:
            raise HTTPException(status_code=500, detail=f"Could not start script: {exc}") from exc

    log_tail = log_path.read_text(errors="replace")[-8000:]
    if return_code:
        raise HTTPException(
            status_code=500,
            detail={
                "job_id": job_id,
                "script": script_name,
                "exit_code": return_code,
                "log_tail": log_tail,
            },
        )

    return {
        "job_id": job_id,
        "script": script_name,
        "status": "completed",
        "log": str(log_path),
        "log_tail": log_tail,
    }


@app.post("/train")
async def train(request: TrainingRequest, _token=Depends(require_api_token)):
    if not DATASET_NAME.fullmatch(request.dataset):
        raise HTTPException(status_code=400, detail="Invalid dataset folder name")

    dataset_dir = (DATASETS_DIR / request.dataset).resolve()
    if not dataset_dir.is_relative_to(DATASETS_DIR) or not dataset_dir.is_dir():
        raise HTTPException(status_code=404, detail="Dataset folder not found")
    if not TRAIN_SCRIPT.is_file():
        raise HTTPException(status_code=500, detail="Training script is missing")

    job_id = str(uuid.uuid4())
    output_dir = MODELS_DIR / job_id
    output_dir.mkdir(parents=True, exist_ok=False)
    log_path = output_dir / "training.log"

    command = [
        "python3",
        str(TRAIN_SCRIPT),
        "--dataset", str(dataset_dir),
        "--output-dir", str(output_dir),
        "--epochs", str(request.epochs),
        "--batch-size", str(request.batch_size),
        "--accumulation-steps", str(request.accumulation_steps),
        "--max-image-size", str(request.max_image_size),
        "--learning-rate", str(request.learning_rate),
    ]

    async with training_slots:
        try:
            with log_path.open("wb") as log_file:
                process = await asyncio.create_subprocess_exec(
                    *command,
                    stdout=log_file,
                    stderr=asyncio.subprocess.STDOUT,
                )
                return_code = await process.wait()
        except OSError as exc:
            raise HTTPException(status_code=500, detail=f"Could not start training: {exc}") from exc

    if return_code:
        log_tail = log_path.read_text(errors="replace")[-8000:]
        raise HTTPException(
            status_code=500,
            detail={"job_id": job_id, "message": "Training failed", "log_tail": log_tail},
        )

    return {
        "job_id": job_id,
        "status": "completed",
        "model": str(output_dir / "cocosynth_model.pth"),
        "metadata": str(output_dir / "cocosynth_model_metadata.json"),
    }
