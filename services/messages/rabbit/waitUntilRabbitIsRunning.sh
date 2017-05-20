#!/usr/bin/env bash

until $(curl --output /dev/null --silent --head --fail http://localhost:15672); do
    printf '.'
    sleep 1
done

echo "Admin interface: http://127.0.0.1:15672"
