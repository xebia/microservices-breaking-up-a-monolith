#!/usr/bin/env bash

for port in 9001 9002 9003 9004 9005; do
    echo waiting for $port
    until $(curl --output /dev/null --silent http://localhost:$port/health); do
        printf '.'
        sleep 1
    done
done
