#!/usr/bin/env bash

set -euxo pipefail

helm repo add ingress-nginx https://kubernetes.github.io/ingress-nginx
helm repo update

kubectl create namespace ingress-nginx
helm install ingress-nginx ingress-nginx/ingress-nginx \
    --namespace ingress-nginx \
    --create-namespace \
    --set controller.replicaCount=2 \
    --set controller.service.type=LoadBalancer \
    --set controller.service.annotations."service\.beta\.kubernetes\.io/aws-load-balancer-type"="nlb"

kubectl create namespace tdk-microservices-demo

kubectl apply -f ./infrastructure/helm/environemnts/"$DEMO_ENV"/films-secret-"$DEMO_ENV".yaml -n tdk-microservices-demo
kubectl apply -f ./infrastructure/helm/environemnts/"$DEMO_ENV"/payments-secret-"$DEMO_ENV".yaml -n tdk-microservices-demo

helm install tdk-microservices-demo ./infrastructure/helm/charts/tdk-microservices-demo \
  --values ./infrastructure/helm/environemnts/"$DEMO_ENV"/values-"$DEMO_ENV".yaml \
  --namespace tdk-microservices-demo
