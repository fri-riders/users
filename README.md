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

App is accessible on port `8082`

# Registered endpoints
## Users
* `GET: /v1/users` Returns list of users
* `POST: /v1/users` Create new user (params: `email:string, password:string`)
* `GET: /v1/users/{uuid}` Returns info about user
* `GET: /v1/users/{uuid}/accommodations` Returns list of users' accommodations
## Config
* `GET: /v1/config` Returns list of config values
* `GET: /v1/config/info` Returns info about project
## Health
* `GET: /health` Returns health status
* `GET: /v1/health-test/instance` Returns info about instance
* `POST: /v1/health-test/update` Update property `healthy` (params: `healthy:boolean`)
* `GET: /v1/health-test/dos/{n}` Execute calculation of n-th Fibonacci number
## Metrics
* `GET: /metrics` Returns metrics