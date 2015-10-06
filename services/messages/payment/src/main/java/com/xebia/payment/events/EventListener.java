package com.xebia.payment.events;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xebia.payment.domain.Orderr;
import com.xebia.payment.domain.Payment;
import com.xebia.payment.repositories.PaymentRepository;
import com.xebia.payment.rest.OrderrResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

@Component
public class EventListener {

    private static Logger LOG = LoggerFactory.getLogger(EventListener.class);
    private ObjectMapper mapper = new ObjectMapper();
    private CountDownLatch latch = new CountDownLatch(1);

    @Autowired
    private PaymentRepository paymentRepository;

    // Exercise 3
    // This is the ItemsOrdered Event, see diagram, domain-meetup-ex3.jpeg
    @RabbitListener(queues = "payment.order")
    public void processOrderMessage(Object message) {
        LOG.info("Message is of type: " + message.getClass().getName());
        if(!(message instanceof byte[])) message = ((Message) message).getBody();
        String content = new String((byte[])message, StandardCharsets.UTF_8);
        LOG.info("Received new order to be paid: " + content);
        try {
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            Orderr orderr = mapper.readValue(content, Orderr.class);
            // Translate Orderr from shopping domain, we should calculate the amount, using address as description for now
            OrderrResource orderrResource = new OrderrResource(orderr.getUuid(), UUID.randomUUID(), 123, "send to " + orderr.getShippingAddress());
            paymentRepository.save(new Payment(orderrResource));
        } catch (Exception e) {
            LOG.error("Error: " + e.getMessage());
        }
        latch.countDown();
    }
}
