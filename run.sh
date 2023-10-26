#!/bin/bash
# Tears down docker containers, rebuilds the services, re-creates containers and starts the services.

if ./gradlew clean build; then
  docker compose down
  docker volume prune -f
  docker compose up -d --build
else
  echo "Build failed, see log above."
fi
