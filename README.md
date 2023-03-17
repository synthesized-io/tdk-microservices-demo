# tdk-microservices-demo

### Commands

Create databases:

```bash
aws cloudformation create-stack --stack-name rds-example --template-body file://deploy/aws/rds/postgres-dbs.yaml --parameters ParameterKey=DBName,ParameterValue=db1 ParameterKey=DBPassword,ParameterValue=password1
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

### Start the app

```bash
docker-compose up --force-recreate --build
```
