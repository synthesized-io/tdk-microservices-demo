#!/usr/bin/env bash

set -euxo pipefail

ECR="$ACCOUNT".dkr.ecr."$REGION".amazonaws.com

aws ecr get-login-password --region "$REGION" | docker login --username AWS --password-stdin "$ECR"

docker build --platform linux/amd64 -t "$ECR"/demo-repo-films:latest . -f Dockerfile.films
docker build --platform linux/amd64 -t "$ECR"/demo-repo-payments:latest . -f Dockerfile.payments

docker push "$ECR"/demo-repo-films:latest
docker push "$ECR"/demo-repo-payments:latest
