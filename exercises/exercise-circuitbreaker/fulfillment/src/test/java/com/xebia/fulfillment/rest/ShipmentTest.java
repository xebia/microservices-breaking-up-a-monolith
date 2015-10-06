package com.xebia.fulfillment.rest;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xebia.fulfillment.FulfillmentApplication;
import com.xebia.fulfillment.domain.*;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.mock.http.MockHttpOutputMessage;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.io.IOException;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = FulfillmentApplication.class)
@WebAppConfiguration
public class ShipmentTest extends TestBase {

    // TODO: add actual test

    protected String json(Object o) throws IOException {
        MockHttpOutputMessage mockHttpOutputMessage = new MockHttpOutputMessage();
        this.mappingJackson2HttpMessageConverter.write(
                o, MediaType.APPLICATION_JSON, mockHttpOutputMessage);
        return mockHttpOutputMessage.getBodyAsString();
    }

    @Test
    public void testAcceptOrderFromShoppingCart() throws Exception {
        // TODO: this uses the string version of the service where we would like to use the Orderr version instead
        // for some reason parsing fails even while using the @JsonIgnoreProperties(ignoreUnknown = true) annotation
        String orderr = "{\"uuid\":\"d45b44e1-4f66-4803-8f1d-9e0b9f2e9651\",\"ordered\":1438319100894,\"shipped\":null,\"shippingAddress\":\"address1\",\"status\":\"Ordered\",\"total\":0.0,\"shoppingCart\":{\"created\":1438319100894,\"lineItems\":[{\"uuid\":\"96d055b0-cb34-49a9-b165-35f5c48b4ccd\",\"quantity\":2,\"product\":{\"uuid\":\"755b9908-0b28-484a-a9cc-4aa44bc5a681\",\"name\":\"product1\",\"supplier\":\"supplier1\",\"price\":10.0},\"price\":10.0}],\"uuid\":\"d415e1c8-ccf6-42ca-9e1d-ff2169860ba8\",\"total\":0.0},\"account\":{\"uuid\":\"15fbad5c-b65c-4d28-b318-49ea334400eb\",\"address\":\"address1\",\"phoneNumber\":\"phoneNumber1\",\"email\":\"email1\"},\"paymentReceived\":false,\"links\":[{\"rel\":\"self\",\"href\":\"http://localhost/cart/orders/c45b44e1-4f66-4803-8f1d-9e0b9f2e9651\"}]}";
        mockMvc.perform(post("/fulfillment/orders/newOrder")
                .content(orderr)
                .contentType(jsonContentType))
                .andExpect(status().isCreated())
        ;
    }

    @Test
    public void testParseOrderFromCartService() {
        try {
            String orderFromCart = "{\"uuid\":\"c45b44e1-4f66-4803-8f1d-9e0b9f2e9651\",\"ordered\":1438319100894,\"shipped\":null,\"shippingAddress\":\"address1\",\"status\":\"Ordered\",\"total\":0.0,\"shoppingCart\":{\"created\":1438319100894,\"lineItems\":[{\"uuid\":\"96d055b0-cb34-49a9-b165-35f5c48b4ccd\",\"quantity\":2,\"product\":{\"uuid\":\"755b9908-0b28-484a-a9cc-4aa44bc5a681\",\"name\":\"product1\",\"supplier\":\"supplier1\",\"price\":10.0},\"price\":10.0}],\"uuid\":\"d415e1c8-ccf6-42ca-9e1d-ff2169860ba8\",\"total\":0.0},\"account\":{\"uuid\":\"15fbad5c-b65c-4d28-b318-49ea334400eb\",\"address\":\"address1\",\"phoneNumber\":\"phoneNumber1\",\"email\":\"email1\"},\"paymentReceived\":false,\"links\":[{\"rel\":\"self\",\"href\":\"http://localhost/cart/orders/c45b44e1-4f66-4803-8f1d-9e0b9f2e9651\"}]}";
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            objectMapper.readValue(orderFromCart, Orderr.class);
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testGetListOfUnpaidOrders() throws Exception {

    }

    @Test
    public void testGetListOfPaidOrders() throws Exception {

    }

    @Test
    public void testGetListOfShippedOrders() throws Exception {

    }
}