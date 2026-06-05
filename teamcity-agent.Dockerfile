FROM jetbrains/teamcity-agent:latest

USER root

# Install basic tools
RUN apt-get update && apt-get install -y \
    curl \
    bash \
    git \
    ca-certificates

# Install kubectl
RUN curl -LO "https://dl.k8s.io/release/v1.29.0/bin/linux/amd64/kubectl" && \
    install -m 0755 kubectl /usr/local/bin/kubectl && \
    rm kubectl

# Install Docker CLI
RUN apt-get update && apt-get install -y docker.io

# Optional: Minikube (only if you REALLY need it inside agent)
RUN curl -LO https://storage.googleapis.com/minikube/releases/latest/minikube-linux-amd64 && \
    install minikube-linux-amd64 /usr/local/bin/minikube && \
    rm minikube-linux-amd64