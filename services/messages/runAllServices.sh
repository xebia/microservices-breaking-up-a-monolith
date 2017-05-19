#!/bin/bash
echo "Stop, cleanup and start RabbitMQ, catalog, fulfillment, payment and shop services"
(cd rabbit && ./runRabbit.sh && sleep 5 && ./RabbitMQSetup.sh)
(cd catalog && ./run.sh)
(cd fulfillment && ./run.sh)
(cd payment && ./run.sh)
(cd shop && ./run.sh)
(cd shopManager && ./run.sh)

echo "This may take a while... check Docker logs to find out if all services have started"

