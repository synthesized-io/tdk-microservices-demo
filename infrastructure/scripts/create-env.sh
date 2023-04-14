#!/usr/bin/env bash

set -euxo pipefail

aws cloudformation create-stack \
  --region "$REGION" \
  --capabilities CAPABILITY_IAM \
  --stack-name demo-eks-cluster-"$DEMO_ENV" \
  --template-body file://infrastructure/cloudformation/demo-cluster.yaml \
  --parameters ParameterKey=FilmsDBPassword,ParameterValue="$FILMS_DB_PASSWORD" \
      ParameterKey=PaymentsDBPassword,ParameterValue="$PAYMENTS_DB_PASSWORD" \
      ParameterKey=Env,ParameterValue="$DEMO_ENV"
