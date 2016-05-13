#!/usr/bin/env bash
# Assuming rabbitmqadmin & rabbitmqctl are available
# and rabbitmq-server has started
RABBIT_MQ_HOST=rabbit

sleep 10
echo "----- ADDING TOPICS TO MESSAGE QUEUE -----"

/rabbitmqadmin -H $RABBIT_MQ_HOST delete exchange name="shop"
/rabbitmqadmin -H $RABBIT_MQ_HOST declare exchange name="shop" type="topic"

/rabbitmqadmin -H $RABBIT_MQ_HOST declare queue name="orderPaid"
/rabbitmqadmin -H $RABBIT_MQ_HOST declare binding source="shop" destination="orderPaid" routing_key="orderPaid"

/rabbitmqadmin -H $RABBIT_MQ_HOST declare queue name="orderCompleted"
/rabbitmqadmin -H $RABBIT_MQ_HOST declare binding source="shop" destination="orderCompleted" routing_key="orderCompleted"

/rabbitmqadmin -H $RABBIT_MQ_HOST declare queue name="orderShipped"
/rabbitmqadmin -H $RABBIT_MQ_HOST declare binding source="shop" destination="orderShipped" routing_key="orderShipped"

/rabbitmqadmin -H $RABBIT_MQ_HOST declare queue name="handlePayment"
/rabbitmqadmin -H $RABBIT_MQ_HOST declare binding source="shop" destination="handlePayment" routing_key="handlePayment"

/rabbitmqadmin -H $RABBIT_MQ_HOST declare queue name="startShopping"
/rabbitmqadmin -H $RABBIT_MQ_HOST declare binding source="shop" destination="startShopping" routing_key="startShopping"

/rabbitmqadmin -H $RABBIT_MQ_HOST declare queue name="handleFulfillment"
/rabbitmqadmin -H $RABBIT_MQ_HOST declare binding source="shop" destination="handleFulfillment" routing_key="handleFulfillment"

/rabbitmqadmin -H $RABBIT_MQ_HOST declare queue name="sessionExpired"
/rabbitmqadmin -H $RABBIT_MQ_HOST declare binding source="shop" destination="sessionExpired" routing_key="sessionExpired"

