#!/bin/bash

docker run -d --name payment -p 9001:9001 xebia/payment_msg_v2

# To start as a normal Java program
# mvn exec:java -Dexec.mainClass="com.xebia.payment.v2.PaymentApplication"

