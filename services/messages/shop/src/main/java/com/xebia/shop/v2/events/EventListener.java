package com.xebia.shop.v2.events;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xebia.shop.v2.Config;
import com.xebia.shop.v2.domain.Clerk;
import com.xebia.shop.v2.domain.ShoppingCart;
import com.xebia.shop.v2.repositories.ClerkRepository;
import com.xebia.shop.v2.repositories.OrderRepository;
import com.xebia.shop.v2.repositories.ShoppingCartRepository;
import com.xebia.shop.v2.repositories.WebUserRepository;
import com.xebia.shop.v2.rest.ShoppingCartController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;


@Component
public class EventListener {

    private static final Logger LOG = LoggerFactory.getLogger(EventListener.class);
    private ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    ShoppingCartController shoppingCartController;

    @Autowired
    ShoppingCartRepository shoppingCartRepository;

    @Autowired
    WebUserRepository webUserRepository;

    @Autowired
    ClerkRepository clerkRepository;

    @RabbitListener(queues = Config.START_SHOPPING)
    public void processStartShoppingMessage(Object message) {
        if (!(message instanceof byte[])) {
            message = ((Message) message).getBody();
        }
        String content = new String((byte[]) message, StandardCharsets.UTF_8);
        LOG.info("Received on START_SHOPPING: " + content);
        try {
            createCart(content);
        } catch (Exception e) {
            LOG.error("error parsing clerk from message: " + content + ", exception: " + e.getMessage());
        }
    }

    public ShoppingCart createCart(Clerk clerk) throws java.io.IOException {
        ShoppingCart cart = shoppingCartRepository.save(new ShoppingCart(new Date(), UUID.randomUUID()));
        clerk.setShoppingCart(cart);
        shoppingCartRepository.save(cart);
        clerkRepository.save(clerk);
        return cart;
    }

    public ShoppingCart createCart(String content) throws java.io.IOException {
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        Clerk clerk = mapper.readValue(content, Clerk.class);
        return createCart(clerk);
    }
}