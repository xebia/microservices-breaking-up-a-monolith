#!/bin/bash

docker run -d --name shop -p 9002:9002 xebia/shop_msg_v2

# To start as a normal Java program
# mvn exec:java -Dexec.mainClass="com.xebia.shop.ShopApplication"

