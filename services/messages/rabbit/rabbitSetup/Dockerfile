FROM alpine

MAINTAINER Pavel Goultiaev

RUN apk add --update bash python

ADD rabbitmqadmin /rabbitmqadmin
ADD RabbitMQSetup.sh /RabbitMQSetup.sh

RUN chmod 755 ./*.sh

ENTRYPOINT [ "/RabbitMQSetup.sh" ]
