FROM jenkins/jenkins:lts-jdk25
USER root
ENV DEBIAN_FRONTEND=noninteractive
RUN apt-get update && apt-get install -y --no-install-recommends docker-cli && rm -rf /var/lib/apt/lists/*
ENV JAVA_HOME=/opt/java/openjdk
ENV PATH=/usr/bin:/opt/java/openjdk/bin:${PATH}
USER jenkins
