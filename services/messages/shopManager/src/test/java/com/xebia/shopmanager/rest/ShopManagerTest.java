package com.xebia.shopmanager.rest;


import com.fasterxml.jackson.databind.DeserializationFeature;
import com.xebia.shopmanager.Config;
import com.xebia.shopmanager.ShopManagerApplication;
import com.xebia.shopmanager.domain.Clerk;
import com.xebia.shopmanager.domain.Session;
import com.xebia.shopmanager.domain.ShopManager;
import com.xebia.shopmanager.domain.WebUser;
import com.xebia.shopmanager.events.EventListener;
import com.xebia.shopmanager.repositories.ClerkRepository;
import com.xebia.shopmanager.repositories.WebUserRepository;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.UUID;
import java.util.concurrent.ExecutionException;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertNull;
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
    }

    @Test
    public void testShoppingProcess() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(clerkController).build();
        Mockito.doNothing().when(rabbitTemplate).convertAndSend(anyString(), anyString(), anyString());
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        WebUser user = createUserThatWillTimeout();
        MvcResult resultActions = mockMvc.perform(post("/shop/session/" + user.getUuid()))
                .andExpect(status().isCreated()).andReturn();
        String data = resultActions.getResponse().getContentAsString();
        Clerk clerk = objectMapper.readValue(data, Clerk.class);
        Session session = shopManager.findSessionByClerk(clerk);
        assertNotNull(clerkRepository.findOne(clerk.getUuid()));

        wait2secondsSoSessionWillBeRemovedAgain();

        verify(rabbitTemplate, times(1)).convertAndSend(eq(Config.shopExchange), eq(Config.startShopping), anyString());
        verify(rabbitTemplate, times(1)).convertAndSend(eq(Config.shopExchange), eq(Config.sessionExpired), anyString());

        eventListener.processSessionExpiredMessage(session);
        assertNull(clerkRepository.findOne(clerk.getUuid()));

        Mockito.doReturn(10000l).when(timeoutPolicy).getTimeout();

        user = createUserThatWillNotTimeout();
        resultActions = mockMvc.perform(post("/shop/session/" + user.getUuid()))
                .andExpect(status().isCreated()).andReturn();
        data = resultActions.getResponse().getContentAsString();
        clerk = objectMapper.readValue(data, Clerk.class);
        assertNotNull(clerkRepository.findOne(clerk.getUuid()));
        wait2secondsSoSessionWillBeRemovedAgain();
        assertNotNull(clerkRepository.findOne(clerk.getUuid()));
    }

    public void testBasicSessionTimeoutBehaviour() throws Exception {
        WebUser user = new WebUser(UUID.randomUUID(), "u", "p");
        Clerk clerk = new Clerk(user);
        shopManager.registerClerk(clerk);
        Session session = shopManager.findSessionByClerk(clerk);
        assertNotNull(session.getClerk());
        wait2secondsSoSessionWillBeRemovedAgain();
        session = shopManager.findSessionByClerk(clerk);
        assertNull(session);
        assertEquals(1, shopManager.getExpiredSessions().size());
    }

    private void wait2secondsSoSessionWillBeRemovedAgain() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            fail("Exception while waiting");
            e.printStackTrace();
        }
    }

    protected WebUser createUserThatWillTimeout() {
        return webUserRepository.save(new WebUser(UUID.randomUUID(), "GenerateTimeOutWhenYouGetThisWebuser", "password"));
    }

    protected WebUser createUserThatWillNotTimeout() {
        return webUserRepository.save(new WebUser(UUID.randomUUID(), "SomeUser", "password"));
    }
}
