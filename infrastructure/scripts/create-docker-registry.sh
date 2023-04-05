#!/usr/bin/env bash

set -euxo pipefail

aws cloudformation create-stack \
  --stack-name demo-ecr-registry-"$SERVICE_NAME" \
  --template-body file://infrastructure/cloudformation/docker-registry.yaml \
  --parameters ParameterKey=ServiceName,ParameterValue="$SERVICE_NAME"

