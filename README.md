[![Build Status](https://travis-ci.org/fri-riders/users.svg?branch=master)](https://travis-ci.org/fri-riders/users)
# Users Service

## Run with Docker Compose
1. Build app: `mvn clean package`
1. Run: `docker-compose up --build`

App is accessible on port `8082`

# Registered endpoints
## Users
* `GET: /v1/users` Returns list of users
* `POST: /v1/users` Create new user (params: `email:string, password:string`)
* `GET: /v1/users/login?email&password` Login user
* `GET: /v1/users/{uuid}` Returns info about user \***
* `PUT: /v1/users/{uuid}` Update user \***
* `DELETE: /v1/users/{uuid}` Delete user user \***
* `GET: /v1/users/{uuid}/accommodations` Returns list of users' accommodations
* `GET: /v1/users/{uuid}/bookings` Returns list of users' bookings
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

#### Notes
\*** must be authenticated with `authToken` header present and supplied with valid JWT from login