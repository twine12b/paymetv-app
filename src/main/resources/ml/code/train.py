import argparse
import gc
import json
import logging
import os
import threading
import time
from pathlib import Path

import torch
import psutil
from torch.utils.data import Dataset, DataLoader
from PIL import Image
import torchvision
from torchvision.transforms import functional as F
from torchvision.models.detection import (
    fasterrcnn_resnet50_fpn_v2,
    FasterRCNN_ResNet50_FPN_V2_Weights,
)

logger = logging.getLogger(__name__)
MAX_MEMORY_BYTES = 2 * 1024 ** 3
PROCESS = psutil.Process(os.getpid())


def manage_memory(device, context):
    memory_bytes = PROCESS.memory_info().rss
    if memory_bytes <= MAX_MEMORY_BYTES:
        return

    logger.warning(
        "Process memory is %.2f GiB after %s (limit %.2f GiB); "
        "running garbage collection and releasing device cache",
        memory_bytes / 1024 ** 3,
        context,
        MAX_MEMORY_BYTES / 1024 ** 3,
    )
    collected_objects = gc.collect()
    if device.type == "cuda":
        torch.cuda.empty_cache()
    elif device.type == "mps":
        torch.mps.empty_cache()

    memory_bytes = PROCESS.memory_info().rss
    logger.info(
        "Memory management after %s: collected %d objects; "
        "process RSS %.2f GiB",
        context,
        collected_objects,
        memory_bytes / 1024 ** 3,
    )
    if memory_bytes > MAX_MEMORY_BYTES:
        raise MemoryError(
            f"Training process uses {memory_bytes / 1024 ** 3:.2f} GiB "
            f"after memory cleanup, exceeding the "
            f"{MAX_MEMORY_BYTES / 1024 ** 3:.0f} GiB limit"
        )

# ============================================================
# MEMORY SETTINGS
# ============================================================
# Prevent PyTorch from creating too many CPU threads.
#
# This is particularly important on machines with limited RAM.
torch.set_num_threads(2)
try:
    torch.set_num_interop_threads(1)
except RuntimeError:
    pass


# ============================================================
# COCO DATASET
# ============================================================
class CocosynthCocoDataset(Dataset):
    def __init__(self, dataset_dir, max_image_size=640):
        self.dataset_dir = Path(dataset_dir)
        self.max_image_size = max_image_size

        if not self.dataset_dir.exists():
            raise FileNotFoundError(
                f"Dataset directory does not exist: "
                f"{self.dataset_dir}"
            )

        # ----------------------------------------------------
        # Find COCO JSON
        # ----------------------------------------------------
        json_files = list(self.dataset_dir.rglob("*.json"))
        if not json_files:
            raise FileNotFoundError(
                f"No JSON annotation file found in "
                f"{self.dataset_dir}"
            )

        preferred = [
            f
            for f in json_files
            if any(
                x in f.name.lower()
                for x in ["coco", "annotation", "instances"]
            )
        ]
        if preferred:
            self.annotation_file = preferred[0]
        else:
            self.annotation_file = json_files[0]

        print()
        print(
            f"Using COCO annotation file:\n"
            f" {self.annotation_file}"
        )

        # ----------------------------------------------------
        # Load COCO JSON
        # ----------------------------------------------------
        with open(self.annotation_file, "r", encoding="utf-8") as f:
            self.coco = json.load(f)

        self.images = self.coco.get("images", [])
        self.annotations = self.coco.get("annotations", [])
        self.categories = self.coco.get("categories", [])

        if not self.images:
            raise ValueError("COCO JSON contains no images")
        if not self.categories:
            raise ValueError("COCO JSON contains no categories")

        # ----------------------------------------------------
        # Category mapping
        # ----------------------------------------------------
        self.category_id_to_label = {}
        for index, category in enumerate(self.categories, start=1):
            self.category_id_to_label[category["id"]] = index

        self.category_names = {
            index: category["name"]
            for index, category in enumerate(self.categories, start=1)
        }

        # ----------------------------------------------------
        # Annotation lookup
        # ----------------------------------------------------
        self.annotations_by_image = {}
        for annotation in self.annotations:
            image_id = annotation["image_id"]
            if image_id not in self.annotations_by_image:
                self.annotations_by_image[image_id] = []
            self.annotations_by_image[image_id].append(annotation)

        print()
        print("Dataset information")
        print("-------------------")
        print(f"Images: {len(self.images)}")
        print(f"Annotations: {len(self.annotations)}")
        print(f"Categories: {len(self.categories)}")
        print(f"Max image size: {max_image_size}")
        print()
        print("Categories:")
        for label, name in self.category_names.items():
            print(f" {label}: {name}")

    # --------------------------------------------------------
    # Locate image
    # --------------------------------------------------------
    def find_image(self, file_name):
        file_name = Path(file_name)
        candidate = self.dataset_dir / file_name
        if candidate.exists():
            return candidate

        candidate = self.annotation_file.parent / file_name
        if candidate.exists():
            return candidate

        matches = list(self.dataset_dir.rglob(file_name.name))
        if matches:
            return matches[0]

        raise FileNotFoundError(
            f"Could not find image "
            f"'{file_name}' under "
            f"{self.dataset_dir}"
        )

    # --------------------------------------------------------
    # Resize image while preserving aspect ratio
    # --------------------------------------------------------
    def resize_image_and_boxes(self, image, boxes):
        width, height = image.size
        largest_dimension = max(width, height)

        if largest_dimension <= self.max_image_size:
            return image, boxes

        scale = self.max_image_size / largest_dimension
        new_width = int(width * scale)
        new_height = int(height * scale)
        image = image.resize(
            (new_width, new_height),
            Image.Resampling.BILINEAR,
        )

        if boxes:
            resized_boxes = []
            for box in boxes:
                xmin, ymin, xmax, ymax = box
                resized_boxes.append(
                    [
                        xmin * scale,
                        ymin * scale,
                        xmax * scale,
                        ymax * scale,
                    ]
                )
            boxes = resized_boxes

        return image, boxes

    # --------------------------------------------------------
    # Dataset length
    # --------------------------------------------------------
    def __len__(self):
        return len(self.images)

    # --------------------------------------------------------
    # Dataset item
    # --------------------------------------------------------
    def __getitem__(self, index):
        image_info = self.images[index]
        image_id = image_info["id"]
        image_path = self.find_image(image_info["file_name"])

        # ----------------------------------------------------
        # Load one image only
        # ----------------------------------------------------
        with Image.open(image_path) as source:
            image = source.convert("RGB")

        annotations = self.annotations_by_image.get(image_id, [])
        boxes = []
        labels = []
        areas = []
        iscrowd = []

        for annotation in annotations:
            if "bbox" not in annotation:
                continue

            x, y, width, height = annotation["bbox"]
            if width <= 0 or height <= 0:
                continue

            category_id = annotation["category_id"]
            label = self.category_id_to_label.get(category_id)
            if label is None:
                continue

            boxes.append([x, y, x + width, y + height])
            labels.append(label)
            areas.append(annotation.get("area", width * height))
            iscrowd.append(annotation.get("iscrowd", 0))

        # ----------------------------------------------------
        # Resize BEFORE converting to tensor
        # ----------------------------------------------------
        image, boxes = self.resize_image_and_boxes(image, boxes)

        # ----------------------------------------------------
        # Convert to tensors
        # ----------------------------------------------------
        if boxes:
            boxes = torch.as_tensor(boxes, dtype=torch.float32)
            labels = torch.as_tensor(labels, dtype=torch.int64)
            areas = torch.as_tensor(areas, dtype=torch.float32)
            iscrowd = torch.as_tensor(iscrowd, dtype=torch.int64)
        else:
            boxes = torch.zeros((0, 4), dtype=torch.float32)
            labels = torch.zeros((0,), dtype=torch.int64)
            areas = torch.zeros((0,), dtype=torch.float32)
            iscrowd = torch.zeros((0,), dtype=torch.int64)

        target = {
            "boxes": boxes,
            "labels": labels,
            "image_id": torch.tensor([image_id]),
            "area": areas,
            "iscrowd": iscrowd,
        }

        image = F.to_tensor(image)
        return image, target


# ============================================================
# COLLATE FUNCTION
# ============================================================
def collate_fn(batch):
    return tuple(zip(*batch))


# ============================================================
# MODEL
# ============================================================
def create_model(number_of_classes):
    print()
    print("Creating Faster R-CNN model...")
    weights = FasterRCNN_ResNet50_FPN_V2_Weights.DEFAULT
    model = fasterrcnn_resnet50_fpn_v2(weights=weights)
    in_features = (
        model.roi_heads
        .box_predictor
        .cls_score
        .in_features
    )
    model.roi_heads.box_predictor = (
        torchvision.models.detection.faster_rcnn.FastRCNNPredictor(
            in_features,
            number_of_classes,
        )
    )
    return model


# ============================================================
# TRAINING
# ============================================================
def train_one_epoch(
    model,
    optimizer,
    data_loader,
    device,
    epoch,
    scaler,
    accumulation_steps,
):
    model.train()
    total_loss = 0.0
    optimizer.zero_grad(set_to_none=True)
    total_batches = len(data_loader)
    epoch_started = time.monotonic()
    logger.info("Epoch %d started (%d batches)", epoch, total_batches)
    progress_state = {"completed_batches": 0, "total_loss": 0.0}
    progress_lock = threading.Lock()
    stop_progress = threading.Event()

    def log_periodic_progress():
        while not stop_progress.wait(30):
            with progress_lock:
                completed_batches = progress_state["completed_batches"]
                running_loss = progress_state["total_loss"]

            elapsed = time.monotonic() - epoch_started
            memory_gib = PROCESS.memory_info().rss / 1024 ** 3
            if completed_batches:
                logger.info(
                    "Epoch %d heartbeat: %d/%d batches completed, "
                    "average loss %.4f, memory %.2f/%.0f GiB, elapsed %.1fs",
                    epoch,
                    completed_batches,
                    total_batches,
                    running_loss / completed_batches,
                    memory_gib,
                    MAX_MEMORY_BYTES / 1024 ** 3,
                    elapsed,
                )
            else:
                logger.info(
                    "Epoch %d heartbeat: processing first batch; "
                    "0/%d batches completed, memory %.2f/%.0f GiB, "
                    "elapsed %.1fs",
                    epoch,
                    total_batches,
                    memory_gib,
                    MAX_MEMORY_BYTES / 1024 ** 3,
                    elapsed,
                )

    progress_thread = threading.Thread(
        target=log_periodic_progress,
        name=f"training-progress-epoch-{epoch}",
        daemon=True,
    )
    progress_thread.start()

    try:
        for batch_index, (images, targets) in enumerate(data_loader):
            images = [
                image.to(device, non_blocking=False)
                for image in images
            ]
            targets = [
                {
                    key: value.to(device, non_blocking=False)
                    for key, value in target.items()
                }
                for target in targets
            ]

            # ----------------------------------------------------
            # Mixed precision
            # ----------------------------------------------------
            use_amp = device.type == "cuda"
            if use_amp:
                with torch.cuda.amp.autocast():
                    loss_dict = model(images, targets)
                    losses = sum(loss for loss in loss_dict.values())
                    losses = losses / accumulation_steps
            else:
                loss_dict = model(images, targets)
                losses = sum(loss for loss in loss_dict.values())
                losses = losses / accumulation_steps

            # ----------------------------------------------------
            # Backpropagation
            # ----------------------------------------------------
            if use_amp:
                scaler.scale(losses).backward()
            else:
                losses.backward()

            # ----------------------------------------------------
            # Gradient accumulation
            # ----------------------------------------------------
            if (batch_index + 1) % accumulation_steps == 0:
                if use_amp:
                    scaler.step(optimizer)
                    scaler.update()
                else:
                    optimizer.step()
                optimizer.zero_grad(set_to_none=True)

            batch_loss = losses.item() * accumulation_steps
            total_loss += batch_loss
            with progress_lock:
                progress_state["completed_batches"] = batch_index + 1
                progress_state["total_loss"] = total_loss

            # ----------------------------------------------------
            # Release references
            # ----------------------------------------------------
            del images
            del targets
            del loss_dict
            del losses
            if device.type == "cuda":
                torch.cuda.empty_cache()
            manage_memory(device, f"batch {batch_index + 1} of epoch {epoch}")
    finally:
        stop_progress.set()
        progress_thread.join()

    # --------------------------------------------------------
    # Handle final partial accumulation
    # --------------------------------------------------------
    if len(data_loader) % accumulation_steps != 0:
        if use_amp:
            scaler.step(optimizer)
            scaler.update()
        else:
            optimizer.step()
        optimizer.zero_grad(set_to_none=True)

    logger.info(
        "Epoch %d completed: %d/%d batches, average loss %.4f, elapsed %.1fs",
        epoch,
        total_batches,
        total_batches,
        total_loss / total_batches,
        time.monotonic() - epoch_started,
    )

    return total_loss / len(data_loader)


# ============================================================
# SAVE MODEL
# ============================================================
def save_model(model, dataset, dataset_dir, epoch):
    dataset_dir = Path(dataset_dir)
    model_path = dataset_dir / "cocosynth_model.pth"
    metadata_path = dataset_dir / "cocosynth_model_metadata.json"

    # --------------------------------------------------------
    # Save CPU version of model
    #
    # This reduces the chance that GPU memory remains occupied
    # by the saved state.
    # --------------------------------------------------------
    cpu_state_dict = {
        key: value.detach().cpu()
        for key, value in model.state_dict().items()
    }
    torch.save(
        {
            "model_state_dict": cpu_state_dict,
            "epoch": epoch,
            "category_id_to_label": dataset.category_id_to_label,
            "category_names": dataset.category_names,
        },
        model_path,
    )
    del cpu_state_dict

    metadata = {
        "model": "Faster R-CNN ResNet50 FPN V2",
        "epoch": epoch,
        "categories": dataset.category_names,
        "category_id_to_label": dataset.category_id_to_label,
        "dataset_directory": str(dataset_dir),
        "annotation_file": str(dataset.annotation_file),
    }
    with open(metadata_path, "w", encoding="utf-8") as f:
        json.dump(metadata, f, indent=4)

    print()
    print(
        f"Model saved to:\n"
        f" {model_path}"
    )
    print(
        f"Metadata saved to:\n"
        f" {metadata_path}"
    )


# ============================================================
# MAIN
# ============================================================
def main():
    logging.basicConfig(
        level=logging.INFO,
        format="%(asctime)s %(levelname)s %(message)s",
        datefmt="%Y-%m-%d %H:%M:%S",
    )
    parser = argparse.ArgumentParser(
        description="Memory-safe Cocosynth COCO training"
    )
    parser.add_argument(
        "--dataset",
        required=True,
        type=Path,
        help="Cocosynth output directory",
    )
    parser.add_argument("--epochs", type=int, default=10)
    parser.add_argument("--batch-size", type=int, default=1)
    parser.add_argument(
        "--accumulation-steps",
        type=int,
        default=1,
        help="Number of batches to accumulate " "before updating the model",
    )
    parser.add_argument(
        "--max-image-size",
        type=int,
        default=640,
        help="Maximum image width/height",
    )
    parser.add_argument("--learning-rate", type=float, default=0.005)
    parser.add_argument("--model", type=str, default="fasterrcnn")
    args = parser.parse_args()

    # --------------------------------------------------------
    # Device
    # --------------------------------------------------------
    if torch.cuda.is_available():
        device = torch.device("cuda")
    elif (
        hasattr(torch.backends, "mps")
        and torch.backends.mps.is_available()
    ):
        device = torch.device("mps")
    else:
        device = torch.device("cpu")

    print()
    print("========================================")
    print("Memory-Safe Cocosynth Training")
    print("========================================")
    print()
    print(f"Dataset: {args.dataset}")
    print(f"Epochs: {args.epochs}")
    print(f"Batch size: {args.batch_size}")
    print(f"Accumulation: {args.accumulation_steps}")
    print(f"Maximum image size: {args.max_image_size}")
    print(f"Learning rate: {args.learning_rate}")
    print(f"Device: {device}")
    print()

    # --------------------------------------------------------
    # Dataset
    # --------------------------------------------------------
    dataset = CocosynthCocoDataset(
        args.dataset,
        max_image_size=args.max_image_size,
    )

    # --------------------------------------------------------
    # DataLoader
    # # num_workers=0 deliberately.
    # #
    # # Additional workers can duplicate memory usage because
    # # several images may be loaded simultaneously.
    # --------------------------------------------------------
    data_loader = DataLoader(
        dataset,
        batch_size=args.batch_size,
        shuffle=True,
        num_workers=0,
        pin_memory=False,
        collate_fn=collate_fn,
    )

    # --------------------------------------------------------
    # Model
    # --------------------------------------------------------
    number_of_classes = len(dataset.categories) + 1
    model = create_model(number_of_classes)
    model.to(device)
    manage_memory(device, "model initialization")

    # --------------------------------------------------------
    # Optimizer
    # --------------------------------------------------------
    parameters = [
        parameter
        for parameter in model.parameters()
        if parameter.requires_grad
    ]
    optimizer = torch.optim.SGD(
        parameters,
        lr=args.learning_rate,
        momentum=0.9,
        weight_decay=0.0005,
    )

    # --------------------------------------------------------
    # Mixed precision scaler
    # --------------------------------------------------------
    scaler = torch.cuda.amp.GradScaler(enabled=device.type == "cuda")

    # --------------------------------------------------------
    # Training
    # --------------------------------------------------------
    print()
    print("Starting training...")
    for epoch in range(1, args.epochs + 1):
        average_loss = train_one_epoch(
            model=model,
            optimizer=optimizer,
            data_loader=data_loader,
            device=device,
            epoch=epoch,
            scaler=scaler,
            accumulation_steps=args.accumulation_steps,
        )

        print()
        logger.info(
            "Epoch %d/%d completed; average loss %.4f",
            epoch,
            args.epochs,
            average_loss,
        )

        # ----------------------------------------------------
        # Save checkpoint
        # ----------------------------------------------------
        save_model(model, dataset, args.dataset, epoch)

        # ----------------------------------------------------
        # Explicit memory cleanup
        # ----------------------------------------------------
        gc.collect()
        if device.type == "cuda":
            torch.cuda.empty_cache()

    print()
    print("========================================")
    print("Training complete")
    print("========================================")


if __name__ == "__main__":
    main()
