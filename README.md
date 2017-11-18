[![Build Status](https://travis-ci.org/fri-riders/users.svg?branch=master)](https://travis-ci.org/fri-riders/users)
# Users Service
## Run manually
1. Build app: `mvn clean package`
1. Start Postgres: `docker run -e POSTGRES_PASSWORD=postgres -p 5432:5432 postgres:10.0`
1. Start Consul: `docker run -p 8500:8500 consul`
1. Run app: `java -jar target/users-1.0-SNAPSHOT.jar`

## Run with Docker Compose
1. Build app: `mvn clean package`
1. Run: `docker-compose up --build`

App is accessible on port 8081