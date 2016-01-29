package com.xebia.shopmanager.rest;


import com.fasterxml.jackson.databind.DeserializationFeature;
import com.xebia.shopmanager.Config;
import com.xebia.shopmanager.ShopManagerApplication;
import com.xebia.shopmanager.domain.*;
import com.xebia.shopmanager.events.EventListener;
import com.xebia.shopmanager.repositories.ClerkRepository;
import com.xebia.shopmanager.repositories.WebUserRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Date;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ShopManagerApplication.class)
@WebAppConfiguration
public class ClerkTest extends TestBase {

    @Mock
    RabbitTemplate rabbitTemplate;

    @Autowired
    WebUserRepository webUserRepository;

    @Autowired
    ClerkRepository clerkRepository;

    @Autowired
    @InjectMocks
    ClerkController clerkController;

    @Autowired
    @InjectMocks
    EventListener eventListener;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testShoppingProcess() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(clerkController).build();
        Mockito.doNothing().when(rabbitTemplate).convertAndSend(anyString(), anyString(), anyString());
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        WebUser user = createAndSaveWebUserNoDetails();
        MvcResult resultActions = mockMvc.perform(post("/shop/session/" + user.getUuid()))
                .andExpect(status().isCreated()).andReturn();
        String data = resultActions.getResponse().getContentAsString();
        Clerk clerk = objectMapper.readValue(data, Clerk.class);
        assertEquals(user.getUsername(), clerk.getWebUser().getUsername());
        verify(rabbitTemplate, times(1)).convertAndSend(eq(Config.shopExchange), eq(Config.startShoppingRoutingKey), anyString());
        Orderr orderr = new Orderr(UUID.randomUUID(), new Date(), "address", "ordered");
        orderr.setTotal(1.0);
        ShoppingCart shoppingCart = new ShoppingCart(new Date(), UUID.randomUUID());
        LineItem lineItem = new LineItem(1,1.0,new Product("p", "s",1.0));
        shoppingCart.addLineItem(lineItem);
        Shipment shipment = new Shipment(UUID.randomUUID(), "ordered", "address");
        Payment payment = new Payment(UUID.randomUUID(), new Date(), 1.0, "desc", "c123", orderr.getUuid());
        clerk.setPayment(payment);
        clerk.setOrderr(orderr);
        clerk.setShipment(shipment);

        Message dummyMessage = new Message(json(clerk).getBytes(), new MessageProperties());
        eventListener.processOrderCompletedMessage(dummyMessage);
        verify(rabbitTemplate, times(1)).convertAndSend(eq(Config.shopExchange), eq(Config.paymentRoutingKey), anyString());

        eventListener.processOrderPaidMessage(dummyMessage);
        verify(rabbitTemplate, times(1)).convertAndSend(eq(Config.shopExchange), eq(Config.fulfillmentRoutingKey), anyString());

        // if orderShipped is received, expect order archived ??
    }

    @Test
    public void testClerkCanBeUpdatedInDatabase() {
        Clerk clerk = new Clerk(new WebUser(UUID.randomUUID(), "name", "password"));
        clerkRepository.save(clerk);

        Clerk clerk2 = new Clerk(clerk.getWebUser(), clerk.getUuid());
        Orderr orderr = new Orderr(UUID.randomUUID(), new Date(), "address", "ordered");
        orderr.setTotal(1.0);
        ShoppingCart shoppingCart = new ShoppingCart(new Date(), UUID.randomUUID());
        LineItem lineItem = new LineItem(1,1.0,new Product("p", "s",1.0));
        shoppingCart.addLineItem(lineItem);
        clerk2.setShoppingCart(shoppingCart);
        clerk2.setOrderr(orderr);
        clerkRepository.save(clerk2);

        Clerk clerk3 = clerkRepository.findOne(clerk2.getUuid());
        assertEquals(clerk2.getUuid(), clerk3.getUuid());
    }

}