#!/usr/bin/env bash
# Assuming rabbitmqadmin & rabbitmqctl are available
# and rabbitmq-server has started
# Run 'runRabbit.sh' to start RabbitMQ in a container
RABBIT_MQ_HOST=192.168.99.100

./rabbitmqadmin -H $RABBIT_MQ_HOST declare exchange name="shop" type="topic"
./rabbitmqadmin -H $RABBIT_MQ_HOST declare queue name="fulfillment.order"
./rabbitmqadmin -H $RABBIT_MQ_HOST declare queue name="payment.order"
./rabbitmqadmin -H $RABBIT_MQ_HOST declare queue name="shop.payment"
./rabbitmqadmin -H $RABBIT_MQ_HOST declare queue name="fulfillment.payment"
./rabbitmqadmin -H $RABBIT_MQ_HOST declare binding source="shop" destination="fulfillment.order" routing_key="orders"
./rabbitmqadmin -H $RABBIT_MQ_HOST declare binding source="shop" destination="payment.order" routing_key="orders"
./rabbitmqadmin -H $RABBIT_MQ_HOST declare binding source="shop" destination="shop.payment" routing_key="payments"
./rabbitmqadmin -H $RABBIT_MQ_HOST declare binding source="shop" destination="fulfillment.payment" routing_key="payments"

# routing key and source are used as parameter in RabbitMQ calls. See e.g. OrderController.createNewOrder in the shop project.
# destination refers to a queue (see above).
# a destination has a listener @RabbitListener(queues = "fulfillment.payment")
# see e.g. Eventlistener in the fulfillment project.