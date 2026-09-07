#!/bin/bash
apt-get update -y
podman-compose down
cd ~/KerjaDekat-ORNeotelemetri15
podman-compose up -d --build
