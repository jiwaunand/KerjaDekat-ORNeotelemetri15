#!/bin/bash
apt-get update -y
apt-get install -y podman podman-compose cockpit cockpit-podman
mkdir -p /etc/containers
echo 'unqualified-search-registries = ["docker.io", "quay.io"]' > /etc/containers/registries.conf
podman pod create --name mainpod
podman-compose up -d --build
systemctl enable --now cockpit.socket
