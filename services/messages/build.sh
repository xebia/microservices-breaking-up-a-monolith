#!/bin/bash

mvn clean install

(cd ./rabbit/rabbitSetup && ./buildDockerImages.sh)