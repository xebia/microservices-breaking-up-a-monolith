#!/usr/bin/env bash

echo "jar file name: $1"
echo "DockerHub username: $2"
echo "LOCAL|DOCKERHUB: $3"
echo "Image name: $4"

if [ $# -lt 4 ]; then
    echo "Usage: buildDockerImage.sh <name of jarfile to run> <username on DockerHub> <LOCAL|DOCKERHUB> <Image name>"
    exit -1
fi

JARFILE=$1
DOCKERHUB_USERNAME=$2
LOCAL_OR_REMOTE=$3
IMAGE_NAME=$4

cat src/main/docker/Dockerfile | sed "s/JARFILENAME/$JARFILE/" > target/Dockerfile

if [ "$LOCAL_OR_REMOTE" == "LOCAL" ]; then
    (cd target &&docker build -t shop-$IMAGE_NAME .)
else
    (cd target &&
      docker build -t shop-$IMAGE_NAME . &&
      docker tag shop-$IMAGE_NAME $DOCKERHUB_USERNAME/shop-$IMAGE_NAME:v1
      docker push $DOCKERHUB_USERNAME/shop-$IMAGE_NAME:v1
    )
fi
