#!/usr/bin/env bash

# TODO: make sure queues are ok by running RabbitMQSetup.sh on container startup
#

docker run -d --hostname my-rabbit --name some-rabbit -p 15672:15672 -p 4369:4369 -p 5672:5672 -p 25672:25672 rabbitmq:3-management
