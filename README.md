# tdk-microservices-demo

## Create databases

```bash
aws cloudformation create-stack --stack-name demo-rds-dbs --template-body file://deploy/aws/rds/postgres-dbs.yaml --parameters ParameterKey=DBName,ParameterValue=db1 ParameterKey=DBPassword,ParameterValue=password1
```

Pagila dump splitting:

```bash
docker run --name pagila-postgres -e POSTGRES_PASSWORD=123 -p 5432:5432 -d postgres
docker exec -it pagila-postgres psql -U postgres -c "create role tdk_user login password 'tdk_user123';"
docker exec -it pagila-postgres psql -U postgres -c "create role cloudsqladmin;"
docker cp <pagila_dummp> pagila-postgres:/tmp/pagila_dump.sql
docker exec -it pagila-postgres psql -U postgres -f /tmp/pagila_dump.sql
docker exec -it pagila-postgres psql -U postgres -c "ALTER SCHEMA public OWNER TO tdk_user;" tdk_sakila_input_1gb
docker exec -it pagila-postgres psql -U postgres -c "drop role cloudsqladmin;" tdk_sakila_input_1gb

python3 -m pip install psycopg2 
python3 deploy/aws/rds/split.py

pg_dump -h localhost -U tdk_user -f "pagila_films.sql" --no-owner pagila_films
pg_dump -h localhost -U tdk_user -f "pagila_payments.sql" --no-owner pagila_payments
```

## Init LFS
- Install Git LFS if it is not already installed, following the instructions at https://git-lfs.com/.

- From the project root directory, execute the following commands:

```bash
git lfs install
git lfs pull
```

## Start the app

```bash
docker-compose up --force-recreate --build
```

## Create EKS cluster

1. Find arn for for EKS cluster IAM role. If doesn't exist follow [this](https://docs.aws.amazon.com/eks/latest/userguide/service_IAM_role.html#create-service-role)

2. Find subnet ids for your default VPC:

```bash
aws ec2 describe-vpcs --filters Name=isDefault,Values=true --query 'Vpcs[0].VpcId' --output text
aws ec2 describe-subnets --filters Name=vpc-id,Values=<vpc id> --query 'Subnets[].SubnetId' --output text
```

Update corresponding values in `deploy/aws/eks/cluster.yaml`, if needed (there are some pre-filled values)

3. Create EKS cluster

```bash
aws cloudformation create-stack --stack-name demo-eks-cluster --template-body file://deploy/aws/eks/demo-cluster.yaml
```
