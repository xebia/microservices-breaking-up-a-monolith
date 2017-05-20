#!/bin/bash
echo "Stop, cleanup and start RabbitMQ, catalog, fulfillment, payment and shop services"
(cd rabbit && ./runRabbit.sh && ./waitUntilRabbitIsRunning.sh && ./RabbitMQSetup.sh)
(cd catalog && ./run.sh)
(cd fulfillment && ./run.sh)
(cd payment && ./run.sh)
(cd shop && ./run.sh)
(cd shopManager && ./run.sh)
./waitForServicesToStart.sh

