FROM jetbrains/teamcity-agent:latest

USER root

RUN apt-get update && apt-get install -y curl git

# install kubectl
RUN curl -LO https://dl.k8s.io/release/v1.29.0/bin/linux/amd64/kubectl && \
    chmod +x kubectl && \
    mv kubectl /usr/local/bin/kubectl && \
    kubectl version --client

# optional: docker CLI (if missing)
RUN apt-get install -y docker.io

USER buildagent