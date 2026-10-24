FROM jenkins/jenkins:lts-jdk25

USER root

ENV DEBIAN_FRONTEND=noninteractive

ARG DOCKER_COMPOSE_VERSION=v2.39.4

RUN apt-get update \
    && apt-get install -y --no-install-recommends \
       docker-cli curl ca-certificates \
    && mkdir -p /usr/local/lib/docker/cli-plugins \
    && curl -fSL \
       "https://github.com/docker/compose/releases/download/${DOCKER_COMPOSE_VERSION}/docker-compose-linux-aarch64" \
       -o /usr/local/lib/docker/cli-plugins/docker-compose \
    && chmod +x /usr/local/lib/docker/cli-plugins/docker-compose \
    && rm -rf /var/lib/apt/lists/*

ENV JAVA_HOME=/opt/java/openjdk
ENV PATH=/usr/bin:/opt/java/openjdk/bin:${PATH}

USER jenkins