package com.xebia.shopmanager.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xebia.shopmanager.Config;
import com.xebia.shopmanager.domain.Clerk;
import com.xebia.shopmanager.domain.Orderr;
import com.xebia.shopmanager.domain.Session;
import com.xebia.shopmanager.domain.ShopManager;
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

    @Autowired
    ShopManager shopManager;

    private static Logger LOG = LoggerFactory.getLogger(EventListener.class);
    private ObjectMapper mapper = new ObjectMapper();
    private CountDownLatch latch = new CountDownLatch(1);

    @RabbitListener(queues = Config.orderCompleted)
    public void processOrderCompletedMessage(Object message) {
        if (!(message instanceof byte[])) message = ((Message) message).getBody();
        String content = new String((byte[]) message, StandardCharsets.UTF_8);
        LOG.info("Received orderCompleted: " + content);
        try {
            getClerkFromMessage(content);
            rabbitTemplate.convertAndSend(Config.shopExchange, Config.handlePayment, content);
            LOG.info("Sent " + content + " to payment");
        } catch (Exception e) {
            LOG.error("Error: " + e.getMessage());
        }
        latch.countDown();
    }

    @RabbitListener(queues = Config.orderPaid)
    public void processOrderPaidMessage(Object message) {
        if (!(message instanceof byte[])) message = ((Message) message).getBody();
        String content = new String((byte[]) message, StandardCharsets.UTF_8);
        LOG.info("Received orderPaid: " + content);
        try {
            Clerk clerk = mapper.readValue(content, Clerk.class);
            clerkRepository.save(clerk);
            rabbitTemplate.convertAndSend(Config.shopExchange, Config.handleFulfillment, content);
            LOG.info("sent " + content + " to fulfillment");
        } catch (Exception e) {
            LOG.error("Error: " + e.getMessage());
        }
        latch.countDown();
    }

    @RabbitListener(queues = Config.orderShipped)
    public void processOrderShippedMessage(Object message) {
        if (!(message instanceof byte[])) message = ((Message) message).getBody();
        String content = new String((byte[]) message, StandardCharsets.UTF_8);
        LOG.info("Received orderShipped: " + content);
        try {
            Clerk clerk = mapper.readValue(content, Clerk.class);
            clerkRepository.save(clerk);
            LOG.info("Session completed");
            shopManager.completeSessionForClerk(clerk);
        } catch (Exception e) {
            LOG.error("Error: " + e.getMessage());
        }
        latch.countDown();
    }

    @RabbitListener(queues = Config.sessionExpired)
    public void sessionExpiredMessage(Object message) {
        if (!(message instanceof byte[])) message = ((Message) message).getBody();
        String content = new String((byte[]) message, StandardCharsets.UTF_8);
        LOG.info("Received sessionExpired: " + content);
        try {
            Session session = mapper.readValue(content, Session.class);
            processSessionExpiredMessage(session);
        } catch (Exception e) {
            LOG.error("Error: " + e.getMessage());
        }
        latch.countDown();
    }

    public void processSessionExpiredMessage(Session session) {
        Clerk clerk = session.getClerk();
        LOG.info("Session expired, Clerk " + clerk + " was removed.");
        clerkRepository.delete(clerk);
    }

    public void getClerkFromMessage(String content) throws Exception {
        Clerk clerk = mapper.readValue(content, Clerk.class);
        LOG.info("clerk: " + clerk);
        clerk = clerkRepository.save(clerk);
        String clerkAsJson = mapper.writeValueAsString(clerk);
        LOG.info("clerk na save: " + clerkAsJson);
        Clerk clerk1 = clerkRepository.findOne(clerk.getUuid());
        Orderr order = clerk1.getOrderr();
        LOG.info("order: " + order);
    }
}
