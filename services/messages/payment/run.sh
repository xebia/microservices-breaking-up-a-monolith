#!/bin/bash

for i in $( docker ps -aq -f name=payment)
do
    docker stop $i
done

for i in $( docker ps -aq -f status=exited)
do
    docker rm $i
done

docker run -d --name payment -p 9001:9001 xebia/payment_msg

# To start as a normal Java program
# mvn exec:java -Dexec.mainClass="com.xebia.payment.PaymentApplication"

