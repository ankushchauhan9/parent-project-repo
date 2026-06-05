FROM jetbrains/teamcity-agent:latest

USER root

RUN apt-get update && apt-get install -y \
    curl \
    git \
    ca-certificates \
    gnupg \
    lsb-release

# Install kubectl
RUN curl -LO "https://dl.k8s.io/release/v1.29.0/bin/linux/amd64/kubectl" && \
    install -m 0755 kubectl /usr/local/bin/kubectl && \
    rm kubectl

# Install Docker CLI (NOT docker.io)
RUN curl -fsSL https://download.docker.com/linux/static/stable/x86_64/docker-24.0.7.tgz | tar xz && \
    mv docker/docker /usr/local/bin/ && \
    rm -rf docker

USER buildagent