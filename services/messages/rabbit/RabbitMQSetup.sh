#!/usr/bin/env bash
# Assuming rabbitmqadmin & rabbitmqctl are available
# and rabbitmq-server has started
# Run 'runRabbit.sh' to start RabbitMQ in a container
RABBIT_MQ_HOST=localhost
DIR=`dirname $0`

$DIR/rabbitmqadmin -H $RABBIT_MQ_HOST delete exchange name="shop"
$DIR/rabbitmqadmin -H $RABBIT_MQ_HOST declare exchange name="shop" type="topic"

$DIR/rabbitmqadmin -H $RABBIT_MQ_HOST declare queue name="orderPaid"
$DIR/rabbitmqadmin -H $RABBIT_MQ_HOST declare binding source="shop" destination="orderPaid" routing_key="orderPaid"

$DIR/rabbitmqadmin -H $RABBIT_MQ_HOST declare queue name="orderCompleted"
$DIR/rabbitmqadmin -H $RABBIT_MQ_HOST declare binding source="shop" destination="orderCompleted" routing_key="orderCompleted"

$DIR/rabbitmqadmin -H $RABBIT_MQ_HOST declare queue name="orderShipped"
$DIR/rabbitmqadmin -H $RABBIT_MQ_HOST declare binding source="shop" destination="orderShipped" routing_key="orderShipped"

$DIR/rabbitmqadmin -H $RABBIT_MQ_HOST declare queue name="handlePayment"
$DIR/rabbitmqadmin -H $RABBIT_MQ_HOST declare binding source="shop" destination="handlePayment" routing_key="handlePayment"

$DIR/rabbitmqadmin -H $RABBIT_MQ_HOST declare queue name="startShopping"
$DIR/rabbitmqadmin -H $RABBIT_MQ_HOST declare binding source="shop" destination="startShopping" routing_key="startShopping"

$DIR/rabbitmqadmin -H $RABBIT_MQ_HOST declare queue name="handleFulfillment"
$DIR/rabbitmqadmin -H $RABBIT_MQ_HOST declare binding source="shop" destination="handleFulfillment" routing_key="handleFulfillment"

$DIR/rabbitmqadmin -H $RABBIT_MQ_HOST declare queue name="sessionExpired"
$DIR/rabbitmqadmin -H $RABBIT_MQ_HOST declare binding source="shop" destination="sessionExpired" routing_key="sessionExpired"

