FROM alpine

MAINTAINER Pavel Goultiaev

RUN apk add --update bash python curl

ADD classes/rabbitmqadmin /rabbitmqadmin
ADD classes/RabbitMQSetup.sh /RabbitMQSetup.sh

RUN chmod 755 /rabbitmqadmin
RUN chmod 755 /RabbitMQSetup.sh

ENTRYPOINT [ "/RabbitMQSetup.sh" ]
