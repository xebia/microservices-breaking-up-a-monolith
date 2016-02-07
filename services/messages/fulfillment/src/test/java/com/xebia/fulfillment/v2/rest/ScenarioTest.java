package com.xebia.fulfillment.v2.rest;


import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xebia.fulfillment.v2.FulfillmentApplication;
import com.xebia.fulfillment.v2.domain.*;
import com.xebia.fulfillment.v2.events.EventListener;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.context.WebApplicationContext;

import java.util.Date;
import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = FulfillmentApplication.class)
@WebAppConfiguration
public class ScenarioTest extends TestBase {
    private static Logger LOG = LoggerFactory.getLogger(ScenarioTest.class);

    @Autowired
    protected EventListener eventListener;

    @Mock
    RabbitTemplate rabbitTemplate;

    ObjectMapper objectMapper = new ObjectMapper();

    @InjectMocks
    @Autowired
    ShipmentController shipmentController;

    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);
        this.mockMvc = webAppContextSetup(webApplicationContext).build();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Test
    public void shipOrder() throws Exception {
        Clerk clerk = new Clerk(new WebUser(UUID.randomUUID(), "username", "password"), UUID.randomUUID());
        clerk.setShoppingCart(new ShoppingCart(new Date(), UUID.randomUUID()));
        clerk.setPayment(new Payment(UUID.randomUUID(), new Date(), 1.0, "desc", "c123"));
        Shipment shipment = eventListener.createShipment(clerk);
        MvcResult resultActions;
        resultActions = mockMvc.perform(put("/fulfillment/v2/shipIt/" + shipment.getUuid())
                .contentType(jsonContentType))
                .andExpect(status().isOk())
                .andReturn()
        ;
        String data = resultActions.getResponse().getContentAsString();
        Shipment shipment2 = objectMapper.readValue(data, Shipment.class);
        assertEquals(Shipment.SHIPPED, shipment2.getStatus());

        resultActions = mockMvc.perform(get("/fulfillment/v2/forClerk/" + clerk.getUuid())).andReturn();
        data = resultActions.getResponse().getContentAsString();
        Shipment shipment3 = objectMapper.readValue(data, Shipment.class);
        assertEquals(shipment3, shipment2);
    }

}