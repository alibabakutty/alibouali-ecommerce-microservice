
FROM jenkins/jenkins:lts-jdk25

USER root

ENV DEBIAN_FRONTEND=noninteractive
ARG DOCKER_COMPOSE_VERSION=v2.39.4

# Install Docker CLI, Git and download Docker Compose for Apple Silicon/ARM64
RUN apt-get update \
    && apt-get install -y --no-install-recommends \
       docker-cli \
       git \
       curl \
       ca-certificates \
    && mkdir -p /usr/local/lib/docker/cli-plugins \
    && curl -fSL \
       "https://github.com/docker/compose/releases/download/${DOCKER_COMPOSE_VERSION}/docker-compose-linux-aarch64" \
       -o /usr/local/lib/docker/cli-plugins/docker-compose \
    && chmod +x /usr/local/lib/docker/cli-plugins/docker-compose \
    && git config --system --add safe.directory \
       /var/jenkins_home/workspace/ali-bouali-ecommerce-ci_main \
    && docker --version \
    && docker compose version \
    && git --version \
    && rm -rf /var/lib/apt/lists/*

ENV JAVA_HOME=/opt/java/openjdk
ENV PATH=/usr/bin:/opt/java/openjdk/bin:${PATH}

USER jenkins
