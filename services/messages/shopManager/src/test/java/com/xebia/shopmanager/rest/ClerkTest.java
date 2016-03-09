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
        verify(rabbitTemplate, times(1)).convertAndSend(eq(Config.shopExchange), eq(Config.startShopping), anyString());
        Message dummyMessage = new Message(json(clerk).getBytes(), new MessageProperties());
        eventListener.processOrderCompletedMessage(dummyMessage);
        verify(rabbitTemplate, times(1)).convertAndSend(eq(Config.shopExchange), eq(Config.handlePayment), anyString());

        eventListener.processOrderPaidMessage(dummyMessage);
        verify(rabbitTemplate, times(1)).convertAndSend(eq(Config.shopExchange), eq(Config.handleFulfillment), anyString());

        // TODO: if orderShipped is received, expect order archived ??
    }

}