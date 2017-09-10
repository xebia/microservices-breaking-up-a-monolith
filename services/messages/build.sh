#!/bin/bash

mvn clean install

(cd ./rabbit/rabbitSetup && ./buildDockerImage.sh)
