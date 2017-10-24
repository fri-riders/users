#!/bin/bash

mvn clean install
STATUS=$?
if [ $STATUS -gt 0 ]; then
	echo "=== mvn clean install FAILED ==="
	exit $STATUS
fi

docker build -t rso-users .
STATUS=$?
if [ $STATUS -gt 0 ]; then
	echo "=== docker build -t rso-users . FAILED ==="
	exit $STATUS
fi

docker rm rso-users
STATUS=$?
if [ $STATUS -gt 1 ]; then
	echo "=== docker rm rso-users FAILED ==="
	exit $STATUS
fi

docker run --name rso-users -p 8080:8080 rso-users
STATUS=$?
if [ $STATUS -gt 0 ]; then
	echo "=== docker run --name rso-users -p 8080:8080 rso-users FAILED ==="
	exit $STATUS
fi
