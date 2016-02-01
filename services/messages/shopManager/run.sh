#!/bin/bash

for i in $( docker ps -aq -f name=shopmanager)
do
    docker stop $i
done

for i in $( docker ps -aq -f status=exited)
do
    docker rm $i
done

docker run -d --name shopmanager -p 9005:9005 xebia/shopmanager

# To start as a normal Java program
#mvn exec:java -Dexec.mainClass="com.xebia.shopmanager.ShopManagerApplication"
