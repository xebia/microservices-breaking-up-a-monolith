package com.xebia.shopmanager.rest;


import com.fasterxml.jackson.databind.DeserializationFeature;
import com.xebia.shopmanager.Config;
import com.xebia.shopmanager.ShopManagerApplication;
import com.xebia.shopmanager.domain.Clerk;
import com.xebia.shopmanager.domain.WebUser;
import com.xebia.shopmanager.events.EventListener;
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
public class XTest extends TestBase {

    @Autowired
    RabbitTemplate rabbitTemplate;
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
//        mockMvc = MockMvcBuilders.standaloneSetup(clerkController).build();
//
//        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//        WebUser user = createAndSaveWebUserNoDetails();
//        MvcResult resultActions = mockMvc.perform(post("/shop/session/" + user.getUuid())).andReturn();
//        String data = resultActions.getResponse().getContentAsString();
//        Clerk clerk = objectMapper.readValue(data, Clerk.class);
        WebUser webUser = new WebUser(UUID.randomUUID(), "user" , "password");
        Clerk c = new Clerk(webUser, UUID.randomUUID());
        Message dummyMessage = new Message(json(c).getBytes(), new MessageProperties());
        eventListener.processOrderCompletedMessage(dummyMessage);
    }

}