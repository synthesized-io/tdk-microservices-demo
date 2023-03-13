# tdk-microservices-demo

### Commands

Create databases:

```bash
aws cloudformation create-stack --stack-name rds-example --template-body file://deploy/aws/rds/postgres-dbs.yaml --parameters ParameterKey=DBName,ParameterValue=db1 ParameterKey=DBPassword,ParameterValue=password1
```
