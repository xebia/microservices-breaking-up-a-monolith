package com.xebia.shopmanager.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xebia.shopmanager.Config;
import com.xebia.shopmanager.domain.Clerk;
import com.xebia.shopmanager.repositories.ClerkRepository;
import com.xebia.shopmanager.repositories.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.CountDownLatch;

// TODO: this is business logic hidden in an interface class, move handling of events (the code following message
// translation) to ClerkController?
@Component
public class EventListener {

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Autowired
    ClerkRepository clerkRepository;

    @Autowired
    OrderRepository orderRepository;

    private static Logger LOG = LoggerFactory.getLogger(EventListener.class);
    private ObjectMapper mapper = new ObjectMapper();
    private CountDownLatch latch = new CountDownLatch(1);

    @RabbitListener(queues = Config.orderCompleted)
    public void processOrderCompletedMessage(Object message) {
        LOG.info("Message is of type: " + message.getClass().getName());
        if (!(message instanceof byte[])) message = ((Message) message).getBody();
        String content = new String((byte[]) message, StandardCharsets.UTF_8);
        LOG.info("Received orderCompleted: " + content);
        try {
            Clerk clerk = mapper.readValue(content, Clerk.class);
            clerkRepository.save(clerk);
            rabbitTemplate.convertAndSend(Config.shopExchange, Config.paymentRoutingKey, content);
        } catch (Exception e) {
            LOG.error("Error: " + e.getMessage());
        }
        // TODO: find Clerk instance and restart timer?

        latch.countDown();
    }


    @RabbitListener(queues = Config.orderPaid)
    public void processOrderPaidMessage(Object message) {
        LOG.info("Message is of type: " + message.getClass().getName());
        if (!(message instanceof byte[])) message = ((Message) message).getBody();
        String content = new String((byte[]) message, StandardCharsets.UTF_8);
        LOG.info("Received orderPaid: " + content);
        try {
            Clerk clerk = mapper.readValue(content, Clerk.class);
            clerkRepository.save(clerk);
            rabbitTemplate.convertAndSend(Config.shopExchange, Config.fulfillmentRoutingKey, content);
        } catch (Exception e) {
            LOG.error("Error: " + e.getMessage());
        }
        // TODO: find Clerk instance and restart timer?
        latch.countDown();
    }

    @RabbitListener(queues = Config.orderShipped)
    public void processOrderShippedMessage(Object message) {
        LOG.info("Message is of type: " + message.getClass().getName());
        if (!(message instanceof byte[])) message = ((Message) message).getBody();
        String content = new String((byte[]) message, StandardCharsets.UTF_8);
        LOG.info("Received orderShipped: " + content);
        try {
            // TODO: complete order
            // TODO: save new Clerk status
        } catch (Exception e) {
            LOG.error("Error: " + e.getMessage());
        }
        // TODO: clean up Clerk instance
        latch.countDown();
    }

}