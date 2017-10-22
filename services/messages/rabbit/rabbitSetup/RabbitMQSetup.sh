#!/usr/bin/env bash

# configure queues
# This script waits until the Rabbit container is running

until $(curl --output /dev/null --silent --head --fail http://rabbit:15672); do
    printf '.'
    sleep 1
done


RABBIT_MQ_HOST=rabbit
DIR=`dirname $0`

$DIR/rabbitmqadmin -H $RABBIT_MQ_HOST delete exchange name="shop"
$DIR/rabbitmqadmin -H $RABBIT_MQ_HOST declare exchange name="shop" type="topic"

$DIR/rabbitmqadmin -H $RABBIT_MQ_HOST declare queue name="ORDER_PAID"
$DIR/rabbitmqadmin -H $RABBIT_MQ_HOST declare binding source="shop" destination="ORDER_PAID" routing_key="ORDER_PAID"

$DIR/rabbitmqadmin -H $RABBIT_MQ_HOST declare queue name="ORDER_COMPLETED"
$DIR/rabbitmqadmin -H $RABBIT_MQ_HOST declare binding source="shop" destination="ORDER_COMPLETED" routing_key="ORDER_COMPLETED"

$DIR/rabbitmqadmin -H $RABBIT_MQ_HOST declare queue name="ORDER_SHIPPED"
$DIR/rabbitmqadmin -H $RABBIT_MQ_HOST declare binding source="shop" destination="ORDER_SHIPPED" routing_key="ORDER_SHIPPED"

$DIR/rabbitmqadmin -H $RABBIT_MQ_HOST declare queue name="HANDLE_PAYMENT"
$DIR/rabbitmqadmin -H $RABBIT_MQ_HOST declare binding source="shop" destination="HANDLE_PAYMENT" routing_key="HANDLE_PAYMENT"

$DIR/rabbitmqadmin -H $RABBIT_MQ_HOST declare queue name="START_SHOPPING"
$DIR/rabbitmqadmin -H $RABBIT_MQ_HOST declare binding source="shop" destination="START_SHOPPING" routing_key="START_SHOPPING"

$DIR/rabbitmqadmin -H $RABBIT_MQ_HOST declare queue name="HANDLE_FULFILLMENT"
$DIR/rabbitmqadmin -H $RABBIT_MQ_HOST declare binding source="shop" destination="HANDLE_FULFILLMENT" routing_key="HANDLE_FULFILLMENT"

$DIR/rabbitmqadmin -H $RABBIT_MQ_HOST declare queue name="SESSION_EXPIRED"
$DIR/rabbitmqadmin -H $RABBIT_MQ_HOST declare binding source="shop" destination="SESSION_EXPIRED" routing_key="SESSION_EXPIRED"
