package com.xebia.fulfillment.v2.rest;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xebia.fulfillment.v2.Config;
import com.xebia.fulfillment.v2.FulfillmentApplication;
import com.xebia.fulfillment.v2.domain.Clerk;
import com.xebia.fulfillment.v2.domain.Shipment;
import com.xebia.fulfillment.v2.events.EventListener;
import com.xebia.fulfillment.v2.repositories.ClerkRepository;
import com.xebia.fulfillment.v2.repositories.ShipmentRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MvcResult;

import java.io.File;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = FulfillmentApplication.class)
@WebAppConfiguration
public class ScenarioTest extends TestBase {
    private static final Logger LOG = LoggerFactory.getLogger(ScenarioTest.class);

    @Autowired
    protected EventListener eventListener;

    @Mock
    RabbitTemplate rabbitTemplate;

    @Autowired
    ShipmentRepository shipmentRepository;

    @Autowired
    ClerkRepository clerkRepository;

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
        File file = new File(getClass().getClassLoader().getResource("basicDocument.json").getFile());
        Clerk clerk = new Clerk(file);
        clerk.setUuid(UUID.randomUUID());
        Shipment shipment1 = eventListener.createShipment(clerk);
        MvcResult resultActions = mockMvc.perform(put("/fulfillment/v2/shipIt/" + shipment1.getUuid())
                .contentType(jsonContentType))
                .andExpect(status().isOk())
                .andReturn();
        String data = resultActions.getResponse().getContentAsString();
        Shipment shipment2 = objectMapper.readValue(data, Shipment.class);
        assertEquals(Shipment.SHIPPED, shipment2.getStatus());

        resultActions = mockMvc.perform(get("/fulfillment/v2/forClerk/" + clerk.getUuid())).andReturn();
        data = resultActions.getResponse().getContentAsString();
        Shipment shipment3 = objectMapper.readValue(data, Shipment.class);
        assertEquals(shipment3, shipment2);
    }

    @Test
    public void testClerkWithFullDetailsIsPostedOnQueue() throws Exception {
        Mockito.doNothing().when(rabbitTemplate).convertAndSend(anyString(), anyString(), anyString());
        ArgumentCaptor<String> argument = ArgumentCaptor.forClass(String.class);
        File file = new File(getClass().getClassLoader().getResource("clerk.json").getFile());
        Clerk clerk = new Clerk(file);
        clerkRepository.save(clerk);
        Shipment shipment = new Shipment(UUID.randomUUID());
        shipmentRepository.save(shipment);
        clerk.setShipment(shipment);
        clerkRepository.save(clerk);
        shipmentController.updateDocument(shipment.getUuid());
        verify(rabbitTemplate, times(1)).convertAndSend(eq(Config.SHOP_EXCHANGE), anyString(), argument.capture());
        // TODO: why is the status SHIPPED?
        assertTrue(argument.getValue().indexOf("\"status\":\"SHIPPED\"") > 0);
        assertTrue(argument.getValue().indexOf("\"cardId\":\"c123\"") > 0);
    }


}