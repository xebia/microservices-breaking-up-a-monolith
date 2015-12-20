#!/usr/bin/env bash

for i in $( docker ps -aq -f name=some-rabbit)
do
    docker stop $i
done

for i in $( docker ps -aq -f status=exited)
do
    docker rm $i
done

docker run -d --hostname my-rabbit --name some-rabbit -p 15672:15672 -p 4369:4369 -p 5672:5672 -p 25672:25672 rabbitmq:3-management
