#!/bin/bash

for i in $( docker ps -aq -f name=fulfillment)
do
    docker stop $i
done

for i in $( docker ps -aq -f status=exited)
do
    docker rm $i
done
