package com.xebia.msa.rest;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xebia.msa.Application;
import com.xebia.msa.domain.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.util.Assert;

import java.util.*;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
    public void createAccountAndOrderStuff() throws Exception {

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        WebUser webUser = createWebUser(objectMapper);

        Account account = createAcccount(objectMapper, webUser);

        ShoppingCart cart = createShoppingCart(objectMapper, webUser);

        Product product = createProduct(objectMapper);

        cart = addProductToCart(objectMapper, cart, product);
        Assert.notEmpty(cart.getLineItems());

        webUser = getWebUser(objectMapper, webUser);

        Orderr orderr = createOrder(objectMapper, webUser);

        addAccountToOrder(account, orderr);

        tryToApproveUnpaidOrder(orderr);
        
        tryToShipUnapprovedOrderr(orderr);

        payForOrder(orderr);

        approveOrder(orderr, status().isOk());
        
        Shipment shipment = checkIfOrderIsShippable(objectMapper, orderr);
        
        shipShipment(shipment);

    }
    
    private void shipShipment(Shipment shipment) throws Exception{
        mockMvc.perform(put("/monolith/shipment/shipIt/" + shipment.getUuid())
                .contentType(jsonContentType))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(Shipment.SHIPPED)))
        ;  	
    }
    
    private Shipment checkIfOrderIsShippable(ObjectMapper objectMapper, Orderr orderr) throws Exception{
        MvcResult resultActions;
        resultActions = mockMvc.perform(get("/monolith/shipment/getByOrder/" + orderr.getUuid())
                .contentType(jsonContentType))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(Shipment.SHIPPABLE)))
                .andReturn()
        ;

        String data = resultActions.getResponse().getContentAsString();
        return objectMapper.readValue(data, Shipment.class);
    }
    
    private void tryToShipUnapprovedOrderr(Orderr orderr) throws Exception {
    	mockMvc.perform(get("/monolith/shipment/getByOrder/" + orderr.getUuid())
                .contentType(jsonContentType))
                .andExpect(status().isNotFound())
        ;
    }

    private void approveOrder(Orderr orderr, ResultMatcher ok) throws Exception {
        mockMvc.perform(put("/monolith/orders/" + orderr.getUuid() + "/approve")
                .contentType(jsonContentType))
                .andExpect(ok)
        ;
    }

    private void payForOrder(Orderr orderr) throws Exception {
        mockMvc.perform(put("/monolith/orders/" + orderr.getUuid() + "/pay")
                .contentType(jsonContentType))
                .andExpect(status().isOk())
        ;
    }

    private void tryToApproveUnpaidOrder(Orderr orderr) throws Exception {
        mockMvc.perform(put("/monolith/orders/" + orderr.getUuid() + "/approve")
                .contentType(jsonContentType))
                .andExpect(status().isForbidden())
        ;
    }

    private void addAccountToOrder(Account account, Orderr orderr) throws Exception {
        mockMvc.perform(put("/monolith/orders/" + orderr.getUuid() + "/account")
                .content(this.json(account))
                .contentType(jsonContentType))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.account.address", is(account.getAddress())))
        ;
    }

    private Orderr createOrder(ObjectMapper objectMapper, WebUser webUser) throws Exception {
        MvcResult resultActions;
        resultActions = mockMvc.perform(post("/monolith/orders/add")
                .content(this.json(webUser.getUuid()))
                .contentType(jsonContentType))
                .andExpect(status().isCreated())
                .andReturn()
        ;

        String data = resultActions.getResponse().getContentAsString();
        return objectMapper.readValue(data, Orderr.class);
    }

    private WebUser getWebUser(ObjectMapper objectMapper, WebUser webUser) throws Exception {
        MvcResult resultActions;
        resultActions = mockMvc.perform(get("/monolith/user/" + webUser.getUuid())
                .contentType(jsonContentType))
                .andReturn()
        ;

        String data = resultActions.getResponse().getContentAsString();
        webUser = objectMapper.readValue(data, WebUser.class);
        return webUser;
    }

    private ShoppingCart addProductToCart(ObjectMapper objectMapper, ShoppingCart cart, Product product) throws Exception {
        MvcResult resultActions;
        NewLineItemResource lineItemResource = new NewLineItemResource(product.getUuid(), 2);
        
        LOG.info("NewLineItem: " + this.json(lineItemResource));

        resultActions = mockMvc.perform(post("/monolith/cart/" + cart.getUuid() + "/add")
                .content(this.json(lineItemResource))
                .contentType(jsonContentType))
                .andExpect(status().isCreated())
                .andReturn()
        ;

        String data = resultActions.getResponse().getContentAsString();
        cart = objectMapper.readValue(data, ShoppingCart.class);
        return cart;
    }

    private Product createProduct(ObjectMapper objectMapper) throws Exception {
        MvcResult resultActions;ProductResource productResource = new ProductResource(UUID.randomUUID(), "product1", "supplier1", 10.0);
        resultActions = mockMvc.perform(post("/monolith/products/")
                .content(this.json(productResource))
                .contentType(jsonContentType))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.supplier", is("supplier1")))
                .andReturn()
        ;

        String data = resultActions.getResponse().getContentAsString();
        return objectMapper.readValue(data, Product.class);
    }

    private ShoppingCart createShoppingCart(ObjectMapper objectMapper, WebUser webUser) throws Exception {
        MvcResult resultActions;
        resultActions = mockMvc.perform(post("/monolith/user/" + webUser.getUuid() + "/cart"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.lineItems", is(new ArrayList<LineItem>())))
                .andReturn()
        ;

        String data = resultActions.getResponse().getContentAsString();
        return objectMapper.readValue(data, ShoppingCart.class);
    }

    private Account createAcccount(ObjectMapper objectMapper, WebUser webUser) throws Exception {
        MvcResult resultActions;
        Account account = new Account("address1", "phoneNumber1", "email1");
        resultActions = mockMvc.perform(post("/monolith/account/user/" + webUser.getUuid())
                .content(this.json(account))
                .contentType(jsonContentType))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.address", is("address1")))
                .andReturn()
        ;

        String data = resultActions.getResponse().getContentAsString();
        account = objectMapper.readValue(data, Account.class);
        return account;
    }

    private WebUser createWebUser(ObjectMapper objectMapper) throws Exception {
        MvcResult resultActions;
        WebUserResource webUserResource = new WebUserResource("webuser1", "password");
        resultActions = mockMvc.perform(post("/monolith/user/register")
                .content(this.json(webUserResource))
                .contentType(jsonContentType))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username", is("webuser1"))).andReturn();

        String data = resultActions.getResponse().getContentAsString();
        return objectMapper.readValue(data, WebUser.class);
    }
}