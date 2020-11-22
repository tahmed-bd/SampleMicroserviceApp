# Sample Microservice Application

    A simple microservice applicaiton to practice basic functionalities of microserive development.

Prerequisite :
- Java 1.8
- Maven 3.6
- Vert.x
- ZooKeeper


## Build & Execution

Compile:
```
$ mvn clean compile
```

Package:
```
$ mvn clean package
```

## Deploy services:

- Run zookeeper in the background
- Test:
  ```
  $ mvn clean test
  ```
 ## Runing the services
     java -jar ./target/apigateway-1.0-SNAPSHOT-fat.jar -conf ./src/main/conf/gateway.json >> ./logs/apigateway.log &
     java -jar ./target/productinfo-1.0-SNAPSHOT-fat.jar -conf ./src/main/conf/product.json >> ./logs/productinfo.log &
 
## APIs:
    Product information       - http://localhost:8000
    API Gateway               - http://localhost:8007
    Home page                 - http://localhost:8007/sma/index.html

## Log files:

    apigateway.log
    productinfo.log

## Note:

    Configuration for services (default.json,gateway.json,product.json) from the path src/main/conf/

## TODO
    Authserver and database integration will be done soon