# tdk-microservices-demo

## Local Development

### Init LFS
- Install Git LFS if it is not already installed, following the instructions at https://git-lfs.com/.

- From the project root directory, execute the following commands:

```bash
git lfs install
git lfs pull
```

### Start the app

```bash
docker-compose up --force-recreate --build
```

## Deploy to AWS

### Requirements

To deploy the demo you need an AWS account.

Also, make sure you have AWS CLI installed: [docs](https://docs.aws.amazon.com/cli/latest/userguide/getting-started-install.html)

### Create environments in AWS

```bash
FILMS_DB_PASSWORD=films123 PAYMENTS_DB_PASSWORD=payments123 DEMO_ENV=prod ./infrastructure/scripts/create-env.sh
FILMS_DB_PASSWORD=films123 PAYMENTS_DB_PASSWORD=payments123 DEMO_ENV=staging ./infrastructure/scripts/create-env.sh
```

This will deploy two envs with EKS cluster and two PostgreSQL DBs.

## Configure kubectl

```bash
aws eks update-kubeconfig --region eu-west-2 --name demo-eks-cluster-$DEMO_ENV
kubectl config get-contexts
```

Copy the `NAME` of our context and set it as the current context:

```bash
kubectl config use-context <NAME>
```

## Create Docker registry for both services

```bash
SERVICE_NAME=films ./infrastructure/scripts/create-docker-registry.sh
SERVICE_NAME=payments ./infrastructure/scripts/create-docker-registry.sh
```
Log in to ECR:
Use the AWS CLI to authenticate your Docker client with your ECR registry. Replace <your-account-id> with your AWS account ID and <your-region> with the AWS region where your ECR repository is located.

```bash
aws ecr get-login-password --region <your-region> | docker login --username AWS --password-stdin <your-account-id>.dkr.ecr.<your-region>.amazonaws.com
```

Build images:

```bash
docker build --platform linux/amd64 -t <your-account-id>.dkr.ecr.<your-region>.amazonaws.com/demo-repo-films:latest . -f Dockerfile.films
docker build --platform linux/amd64 -t <your-account-id>.dkr.ecr.<your-region>.amazonaws.com/demo-repo-payments:latest . -f Dockerfile.payments
```

Push images:

```bash
docker push <your-account-id>.dkr.ecr.<your-region>.amazonaws.com/demo-repo-films:latest
docker push <your-account-id>.dkr.ecr.<your-region>.amazonaws.com/demo-repo-payments:latest
```

## Deploy services

Deploy ingress controller:

```bash
helm repo add ingress-nginx https://kubernetes.github.io/ingress-nginx
helm repo update

kubectl create namespace ingress-nginx

helm install ingress-nginx ingress-nginx/ingress-nginx \
    --namespace ingress-nginx \
    --create-namespace \
    --set controller.replicaCount=2 \
    --set controller.service.type=LoadBalancer \
    --set controller.service.annotations."service\.beta\.kubernetes\.io/aws-load-balancer-type"="nlb"
```
Get the hostname of the ingress controller:
```bash
kubectl get svc -n ingress-nginx ingress-nginx-controller -o jsonpath='{.status.loadBalancer.ingress[0].hostname}'
```

You can create an A record in Route53 for this NLB.

Now, let's switch to staging context and deploy the app:

```bash
kubectl config use-context <staging name>
```

Create a namespace for the demo:
```bash
kubectl create namespace tdk-microservices-demo
```

Deploy secrets (you need to create these files using provided examples):
```bash
kubectl apply -f ./infrastructure/helm/environemnts/staging/films-secret-staging.yaml -n tdk-microservices-demo
kubectl apply -f ./infrastructure/helm/environemnts/staging/payments-secret-staging.yaml -n tdk-microservices-demo
```

Deploy the app:
```bash
helm install tdk-microservices-demo ./infrastructure/helm/charts/tdk-microservices-demo \
  --values ./infrastructure/helm/environemnts/staging/values-staging.yaml \
  --namespace tdk-microservices-demo
```

Same procedure for production:
```bash
kubectl config use-context <prod name>
```

Create a namespace for the demo:
```bash
kubectl create namespace tdk-microservices-demo
```

Deploy secrets (you need to create these files using provided examples):
```bash
kubectl apply -f ./infrastructure/helm/environemnts/staging/films-secret-prod.yaml -n tdk-microservices-demo
kubectl apply -f ./infrastructure/helm/environemnts/staging/payments-secret-prod.yaml -n tdk-microservices-demo
```

Deploy the app:
```bash
helm install tdk-microservices-demo ./infrastructure/helm/charts/tdk-microservices-demo \
  --values ./infrastructure/helm/environemnts/staging/values-prod.yaml \
  --namespace tdk-microservices-demo
```

Check status of the deployment:
```bash
kubectl get pods -n tdk-microservices-demo
```

## Appendix: Pagila dump splitting:

```bash
docker run --name pagila-postgres -e POSTGRES_PASSWORD=123 -p 5432:5432 -d postgres
docker exec -it pagila-postgres psql -U postgres -c "create role tdk_user login password 'tdk_user123';"
docker exec -it pagila-postgres psql -U postgres -c "create role cloudsqladmin;"
docker cp <pagila_dump> pagila-postgres:/tmp/pagila_dump.sql
docker exec -it pagila-postgres psql -U postgres -f /tmp/pagila_dump.sql
docker exec -it pagila-postgres psql -U postgres -c "ALTER SCHEMA public OWNER TO tdk_user;" tdk_sakila_input_1gb
docker exec -it pagila-postgres psql -U postgres -c "drop role cloudsqladmin;" tdk_sakila_input_1gb

python3 -m pip install psycopg2 
python3 infrastructure/scripts/split.py

pg_dump -h localhost -U tdk_user -f "pagila_films.sql" --no-owner pagila_films
pg_dump -h localhost -U tdk_user -f "pagila_payments.sql" --no-owner pagila_payments
```
