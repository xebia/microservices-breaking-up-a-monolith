package com.xebia.shop.rest.com.xebia.shop2.rest;


import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xebia.shop.v2.rest.NewLineItemResource;
import com.xebia.shop.v2.ShopApplication;
import com.xebia.shop.v2.domain.*;
import com.xebia.shop.v2.events.EventListener;
import com.xebia.shop.v2.repositories.ClerkRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MvcResult;

import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ShopApplication.class)
@WebAppConfiguration
public class ShoppingCartTest extends TestBase {

    @Autowired
    EventListener eventListener;
    @Autowired
    ClerkRepository clerkRepository;

    @Autowired
    ApplicationContext applicationContext;

    @Test
    public void addProductToCart() throws Exception {
        ShoppingCart cart = eventListener.createCart(new Clerk(new WebUser(UUID.randomUUID(), "username", "password"), UUID.randomUUID()));
        Product product = createAndSaveProduct();
        NewLineItemResource lineItem = new NewLineItemResource(product.getUuid(), 10);
        mockMvc.perform(post("/shop/v2/cart/" + cart.getUuid() + "/add")
                .content(this.json(lineItem))
                .contentType(jsonContentType))
                .andExpect(status().isCreated())
        ;
    }

    @Test
    public void createOrderFromShoppingCart() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        Clerk clerk = new Clerk(new WebUser(UUID.randomUUID(), "username", "password"), UUID.randomUUID());
        ShoppingCart cart = eventListener.createCart(clerk);
        MvcResult resultActions;
        resultActions = mockMvc.perform(post("/shop/v2/cart/" + cart.getUuid() + "/order")
                .contentType(jsonContentType))
                .andExpect(status().isCreated())
                .andReturn()
        ;

        String data = resultActions.getResponse().getContentAsString();
        Orderr orderr = objectMapper.readValue(data, Orderr.class);
        assertNull(orderr.getShippingAddress());

        Clerk clerk2 = clerkRepository.findByOrderr(orderr);
        assertEquals(clerk.getUuid(), clerk2.getUuid());
    }


    @Test
    public void getCart() throws Exception {
        Clerk clerk = new Clerk(new WebUser(UUID.randomUUID(), "username", "password"), UUID.randomUUID());
        ShoppingCart cart = eventListener.createCart(clerk);
        mockMvc.perform(get("/shop/v2/cart/" + cart.getUuid())
                .contentType(jsonContentType))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.uuid", is(cart.getUuid().toString())))
        ;
    }
}