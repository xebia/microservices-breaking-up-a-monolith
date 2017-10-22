#!/bin/bash

# Remove first so container will be rebuild even if only the setup script changes
docker rmi -f xebia/rabbitmq_msg_setup

docker build -t xebia/rabbitmq_msg_setup .
