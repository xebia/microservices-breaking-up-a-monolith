package com.xebia.fulfillment.rest;


import com.xebia.fulfillment.Application;
import com.xebia.fulfillment.domain.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MvcResult;

import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
public class ScenarioTest extends TestBase {
    private static Logger LOG = LoggerFactory.getLogger(ScenarioTest.class);

    @Test
    public void testShipmentBecomesShippableWhenShipItMethodIsCalled() throws Exception {

        Product product = new Product("product1", "supplier1", 10.0);
        LineItem item = new LineItem(UUID.randomUUID(), 2, 20.0, product);

        Account account = new Account(UUID.randomUUID(), "address 1", "035-5381921", "info@xebia.com");
        Orderr orderr = new Orderr(UUID.randomUUID(), "shipping address 1", account);
        orderr.addLineItem(item);
        MvcResult resultActions = mockMvc.perform(post("/fulfillment/orders/")
                .content(this.json(orderr))
                .contentType(jsonContentType))
                .andExpect(status().isCreated())
                .andReturn()
        ;
        String data = resultActions.getResponse().getContentAsString();
        LOG.info("order from fulfillment/orders service: " + data);

        mockMvc.perform(put("/fulfillment/shipIt/" + orderr.getUuid()))
                .andExpect(status().isPaymentRequired())
        ;

        mockMvc.perform(put("/fulfillment/orders/" + orderr.getUuid() + "/paymentReceived"))
                .andExpect(status().isOk())
        ;

        mockMvc.perform(put("/fulfillment/shipIt/" + orderr.getUuid()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(Shipment.SHIPPED)))
        ;
    }
}