#!/bin/bash

docker run -d --name catalog -p 9004:9004 xebia/catalog_msg

# To start as a normal Java program
#mvn exec:java -Dexec.mainClass="com.xebia.catalog.CatalogApplication"
