#!/bin/bash

for i in $( docker ps -aq -f name=fulfillment)
do
    docker stop $i
done

for i in $( docker ps -aq -f status=exited)
do
    docker rm $i
done

docker run -d --name fulfillmennt -p 9004:9004 xebia/fulfillment_msg

# To start as a normal Java program
# mvn exec:java -Dexec.mainClass="com.xebia.fulfillment.FulfillmentApplication"

