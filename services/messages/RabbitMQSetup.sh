#!/usr/bin/env bash
#Assuming rabbitmqadmin & rabbitmqctl are available
# and rabbitmq-server has started

#Reset RabbitMQ to start fresh
rabbitmqctl stop_app
rabbitmqctl reset
rabbitmqctl start_app

#RabbitMQ should now be reachable at: http://localhost:15672 login with guest/guest

#Create exchange, queues and bindings
#Run "rabbitmqadmin help subcommands" to get details on these commands


#declare exchange name=... type=... [auto_delete=... internal=... durable=... arguments=...]
rabbitmqadmin declare exchange name="shop" type="topic"

#declare queue name=... [node=... auto_delete=... durable=... arguments=...]
rabbitmqadmin declare queue name="fulfillment.order"
rabbitmqadmin declare queue name="payment.order"
rabbitmqadmin declare queue name="shop.payment"
rabbitmqadmin declare queue name="fulfillment.payment"

#declare binding source=... destination=... [arguments=... routing_key=... destination_type=...]
rabbitmqadmin declare binding source="shop" destination="fulfillment.order" routing_key="orders"
rabbitmqadmin declare binding source="shop" destination="payment.order" routing_key="orders"
rabbitmqadmin declare binding source="shop" destination="shop.payment" routing_key="payments"
rabbitmqadmin declare binding source="shop" destination="fulfillment.payment" routing_key="payments"

# routing key and source are used as parameter in RabbitMQ calls. See e.g. OrderController.createNewOrder in the shop project.
# destination refers to a queue (see above).
# a destination has a listener @RabbitListener(queues = "fulfillment.payment")
# see e.g. Eventlistener in the fulfillment project.