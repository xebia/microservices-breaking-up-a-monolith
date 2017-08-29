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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import static junit.framework.TestCase.*;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ShopManagerApplication.class)
@WebAppConfiguration
public class ShopManagerTest extends TestBase {
    private static final Logger LOG = LoggerFactory.getLogger(ShopManagerTest.class);

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

    @Autowired
    @InjectMocks
    ShopManager shopManager;

    @Mock
    TimeoutPolicy timeoutPolicy;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        Mockito.doReturn(100l).when(timeoutPolicy).getTimeout();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mockMvc = MockMvcBuilders.standaloneSetup(clerkController).build();
        Mockito.doNothing().when(rabbitTemplate).convertAndSend(anyString(), anyString(), anyString());
    }

    @Test
    public void testSessionIsCreatedForAClerk() throws Exception {
        WebUser user = createUserThatWillTimeout();
        MvcResult resultActions = mockMvc.perform(post("/shop/session/" + user.getUuid()))
                .andExpect(status().isCreated()).andReturn();
        String data = resultActions.getResponse().getContentAsString();
        Clerk clerk = objectMapper.readValue(data, Clerk.class);
        Session session = shopManager.findSessionByClerk(clerk);
        assertNotNull(clerkRepository.findOne(session.getClerk().getUuid()));
    }

    @Test
    public void testSessionExpiredEventIsSent() throws Exception {
        WebUser user = createUserThatWillTimeout();
        MvcResult resultActions = mockMvc.perform(post("/shop/session/" + user.getUuid()))
                .andExpect(status().isCreated()).andReturn();
        String data = resultActions.getResponse().getContentAsString();
        objectMapper.readValue(data, Clerk.class);

        wait2secondsSoSessionWillBeRemovedAgain();

        verify(rabbitTemplate, times(1)).convertAndSend(eq(Config.SHOP_EXCHANGE), eq(Config.START_SHOPPING), anyString());
        verify(rabbitTemplate, times(1)).convertAndSend(eq(Config.SHOP_EXCHANGE), eq(Config.SESSION_EXPIRED), anyString());
    }

    @Test
    public void testSessionIsCompletedWhenOrderShippedIsReceived() throws Exception {
        Mockito.doReturn(10000l).when(timeoutPolicy).getTimeout();
        WebUser user = createUserThatWillNotTimeout();
        MvcResult resultActions = mockMvc.perform(post("/shop/session/" + user.getUuid()))
                .andExpect(status().isCreated()).andReturn();
        String data = resultActions.getResponse().getContentAsString();
        Clerk clerk = objectMapper.readValue(data, Clerk.class);
        Session session = shopManager.findSessionByClerk(clerk);
        Message message = new Message(objectMapper.writeValueAsBytes(clerk), new MessageProperties());
        eventListener.processOrderShippedMessage(message);
        assertFalse(shopManager.getSessions().contains(session));
        assertFalse(shopManager.getExpiredSessions().contains(session));
    }

    @Test
    public void testBasicSessionTimeoutBehaviour() throws Exception {
        WebUser user = new WebUser(UUID.randomUUID(), "u", "p");
        Clerk clerk = new Clerk(user);
        shopManager.registerClerk(clerk);
        Session session = shopManager.findSessionByClerk(clerk);
        assertNotNull(session.getClerk());
        wait2secondsSoSessionWillBeRemovedAgain();
        assertTrue(shopManager.getExpiredSessions().contains(session));
    }

    private void wait2secondsSoSessionWillBeRemovedAgain() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            LOG.trace("Error in sleep " + e.getMessage());
            fail("Exception while waiting");
            Thread.currentThread().interrupt();
        }
    }

    protected WebUser createUserThatWillTimeout() {
        return webUserRepository.save(new WebUser(UUID.randomUUID(), "GenerateTimeOutWhenYouGetThisWebuser", "password"));
    }

    protected WebUser createUserThatWillNotTimeout() {
        return webUserRepository.save(new WebUser(UUID.randomUUID(), "SomeUser", "password"));
    }
}
