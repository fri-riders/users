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

docker stop rso-users
docker rm rso-users
STATUS=$?
if [ $STATUS -gt 1 ]; then
	echo "=== docker rm rso-users FAILED ==="
	exit $STATUS
fi

docker stop etcd
docker rm etcd
STATUS=$?
if [ $STATUS -gt 1 ]; then
	echo "=== docker rm etcd FAILED ==="
	exit $STATUS
fi

 $ docker run -d -p 2379:2379 \
     --name etcd \
     --volume=/tmp/etcd-data:/etcd-data \
     quay.io/coreos/etcd:latest \
     /usr/local/bin/etcd \
     --name my-etcd-1 \
     --data-dir /etcd-data \
     --listen-client-urls http://0.0.0.0:2379 \
     --advertise-client-urls http://0.0.0.0:2379 \
     --listen-peer-urls http://0.0.0.0:2380 \
     --initial-advertise-peer-urls http://0.0.0.0:2380 \
     --initial-cluster my-etcd-1=http://0.0.0.0:2380 \
     --initial-cluster-token my-etcd-token \
     --initial-cluster-state new \
     --auto-compaction-retention 1 \
     -cors="*"
STATUS=$?
if [ $STATUS -gt 0 ]; then
	echo "=== docker run etcd FAILED ==="
	exit $STATUS
fi

docker run --name rso-users -p 8080:8080 rso-users
STATUS=$?
if [ $STATUS -gt 0 ]; then
	echo "=== docker run --name rso-users -p 8080:8080 rso-users FAILED ==="
	exit $STATUS
fi
