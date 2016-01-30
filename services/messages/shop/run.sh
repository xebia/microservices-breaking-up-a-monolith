#!/bin/bash

for i in $( docker ps -aq -f name=shop)
do
    docker stop $i
done

for i in $( docker ps -aq -f status=exited)
do
    docker rm $i
done

docker run -d --name shop -p 9002:9002 xebia/shop_msg_v2

# To start as a normal Java program
# mvn exec:java -Dexec.mainClass="com.xebia.shop.ShopApplication"

