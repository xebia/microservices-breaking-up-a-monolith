#!/bin/bash
echo "Stop, cleanup and start RabbitMQ, catalog, fulfillment, payment and shop services"
(cd rabbit && ./kill.sh)
(cd catalog && ./kill.sh)
(cd fulfillment && ./kill.sh)
(cd payment && ./kill.sh)
(cd shop && ./kill.sh)
(cd shopManager && ./kill.sh)

