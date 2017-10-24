#!/bin/bash

mvn clean install
docker build -t rso-users .
docker rm rso-users
docker run --name rso-users -p 8080:8080 rso-users
