#!/bin/bash

docker build -t rabbitmq_msg_setup .
docker tag rabbitmq_msg_setup jvermeir/shop-rabbitmq_msg_setup:v1
docker push jvermeir/shop-rabbitmq_msg_setup:v1
