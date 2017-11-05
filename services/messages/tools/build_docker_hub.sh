#!/bin/bash

mvn clean install -DlocalOrRemote=DOCKERHUB -DdockerHubUsername=$1
