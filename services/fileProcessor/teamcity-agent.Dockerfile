FROM jetbrains/teamcity-agent:latest

USER root

RUN apt-get update && apt-get install -y curl git

# install kubectl
RUN curl -LO https://dl.k8s.io/release/v1.29.0/bin/linux/amd64/kubectl && \
    install kubectl /usr/local/bin/kubectl

USER buildagent