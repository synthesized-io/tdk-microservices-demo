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

Before you start, you need a EC2 ssh key. If you don't have one, create one in the aws console and copy it's name:

```bash
export EC2_SSH_KEY=<NAME>
```

Also, define db passwords:

```bash
expot FILMS_DB_PASSWORD=<PASSWORD>
expot PAYMENTS_DB_PASSWORD=<PASSWORD>
```

And finally, the environment:

```bash
export DEMO_ENV=prod
```

Example:

```bash
FILMS_DB_PASSWORD=films123 PAYMENTS_DB_PASSWORD=payments123 DEMO_ENV=prod EC2_SSH_KEY=denis ./infrastructure/scripts/deploy.sh
FILMS_DB_PASSWORD=films123 PAYMENTS_DB_PASSWORD=payments123 DEMO_ENV=staging EC2_SSH_KEY=denis ./infrastructure/scripts/deploy.sh
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
