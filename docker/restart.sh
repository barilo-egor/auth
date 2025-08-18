#!/bin/sh
docker stop auth
docker rm -f auth && docker build "$@" -t auth . && \
docker run -it -d --name auth --cpus="2"  --memory="1g" --memory-reservation="1g"  --network tgb --restart always --dns 8.8.8.8 -p 8084:8080 -p 127.0.0.1:5008:5005 -v /home/tgb/docker/data/auth:/root/tgb auth