#!/bin/bash
# Wipes out Axon and Postgres stores and restarts everything

docker compose down
docker volume prune -f
docker compose up -d --build
