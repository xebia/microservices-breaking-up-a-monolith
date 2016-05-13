#!/bin/bash
echo "Stop, cleanup and start RabbitMQ, catalog, fulfillment, payment and shop services"
(cd catalog && mvn package)
(cd fulfillment && mvn package)
(cd payment && mvn package)
(cd shop && mvn package)
(cd shopManager && mvn package)

echo "This may take a while... check Docker logs to find out if all services have started"
