ARG AWS_EMULATOR_PLATFORM=linux/amd64
FROM --platform=${AWS_EMULATOR_PLATFORM} debian:bookworm-slim

ENV DEBIAN_FRONTEND=noninteractive

# install aws-emulator
RUN apt-get update \
    && apt-get install -y --no-install-recommends ca-certificates curl openssl \
    && curl -fsSLo /tmp/aws-emulator.deb https://downloads.aws.com/aws-emulator/packages/aws-emulator/LTS/latest/aws-emulator_amd64.deb \
    && apt-get install -y --no-install-recommends /tmp/aws-emulator.deb \
    && apt-get install less -y \
    && rm -f /tmp/aws-emulator.deb \
    && rm -rf /var/lib/apt/lists/*

# install awscli v2 and kubectl for testing purposes
RUN apt-get update \
    && apt install zip -y \
    && curl "https://awscli.amazonaws.com/awscli-exe-linux-x86_64.zip" -o "awscliv2.zip" \
    && unzip awscliv2.zip \
    && ./aws/install

# install kubectl for testing purposes
RUN apt-get update \
    && curl -O https://s3.us-west-2.amazonaws.com/amazon-eks/1.36.2/2026-07-05/bin/linux/amd64/kubectl \
    && chmod +x ./kubectl \
    && mkdir -p $HOME/bin && cp ./kubectl $HOME/bin/kubectl && export PATH=$HOME/bin:$PATH \
    && echo 'export PATH=$HOME/bin:$PATH' >> ~/.bashrc

# Configure aws-emulator
ENV HOME=/home/emulator
RUN mkdir -p /home/emulator/.aws/emulator/9297

COPY config /home/emulator/.aws/emulator/9297/config

ENV AWS_ENDPOINT_URL=https://127.0.0.1:9297 \
    AWS_CA_BUNDLE=/home/emulator/.aws/emulator/9297/ca.pem \
    AWS_SHARED_CREDENTIALS_FILE=/home/emulator/.aws/emulator/9297/emulator_credentials \
    AWS_CONFIG_FILE=/home/emulator/.aws/emulator/9297/config
#    aws sts get-caller-identity

RUN aws-emulator -v

EXPOSE 9297

ENTRYPOINT ["/usr/bin/aws-emulator"]
CMD ["--telemetry", "off", "--bind", "0.0.0.0", "--port", "9297"]
