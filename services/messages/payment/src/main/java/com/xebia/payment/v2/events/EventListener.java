package com.xebia.payment.v2.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xebia.payment.v2.Config;
import com.xebia.payment.v2.domain.Clerk;
import com.xebia.payment.v2.domain.Payment;
import com.xebia.payment.v2.repositories.ClerkRepository;
import com.xebia.payment.v2.repositories.PaymentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

@Component
public class EventListener {

    private static Logger LOG = LoggerFactory.getLogger(EventListener.class);
    private ObjectMapper mapper = new ObjectMapper();
    private CountDownLatch latch = new CountDownLatch(1);

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private ClerkRepository clerkRepository;

    @RabbitListener(queues = Config.handlePayment)
    public void processPaymentMessage(Object message) {
        if (!(message instanceof byte[])) message = ((Message) message).getBody();
        String content = new String((byte[]) message, StandardCharsets.UTF_8);
        LOG.info("Received new order to be paid: " + content);
        try {
            createPayment(content);
        } catch (Exception e) {
            LOG.error("Error: " + e.getMessage());
        }
        latch.countDown();
    }

    public Payment createPayment(Clerk clerk) throws Exception {
        Payment payment = new Payment(UUID.randomUUID());
        paymentRepository.save(payment);
        clerk.setPayment(payment);
        clerkRepository.save(clerk);
        LOG.info("Created payment for clerk: " + mapper.writeValueAsString(clerk));
        return payment;
    }

    public Payment createPayment(String content) throws Exception {
        Clerk clerk = new Clerk(content);
        return createPayment(clerk);
    }
}
