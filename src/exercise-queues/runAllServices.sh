#!/bin/bash
cd catalog
nohup ./run.sh &
cd -
cd fulfillment
nohup ./run.sh &
cd -
cd payment
nohup ./run.sh &
cd -
cd shop
nohup ./run.sh &
cd -
