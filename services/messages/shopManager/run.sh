#!/bin/bash

docker run -d --name shopmanager -p 9005:9005 xebia/shopmanager

# To start as a normal Java program
#mvn exec:java -Dexec.mainClass="com.xebia.shopmanager.ShopManagerApplication"
