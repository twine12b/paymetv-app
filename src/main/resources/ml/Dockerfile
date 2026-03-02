# Installing CUDA and Tensorflow https://youtu.be/X4F80-CSbig?si=mtZQ74IGFqhaUObO
FROM nvidia/cuda:12.2.0-base-ubuntu22.04

# Install Python and pip
RUN apt-get update && apt-get install -y --no-install-recommends \
    python3 \
    python3-pip \
    python3-dev \
    git \
    && \
    rm -rf /var/lib/apt/lists/*

COPY requirements.txt /tmp/requirements.txt
RUN pip3 install -r /tmp/requirements.txt

# Clone cocosynth into /opt
RUN rm -rf /opt/cocosynth 2>/dev/null || true
RUN git clone https://github.com/akTwelve/cocosynth.git /opt/cocosynth
RUN pip3 install -r /opt/cocosynth/requirements.txt
COPY /code/ /opt/cocosynth/code/


# https://technofob.com/2019/06/14/how-to-compile-tensorflow-2-0-with-avx2-fma-instructions-on-mac/
#RUN #pip install tensorflow-cpu-optimized tensorflow-io # -avx2 -fma, tensorflow-cpu-optimized,
