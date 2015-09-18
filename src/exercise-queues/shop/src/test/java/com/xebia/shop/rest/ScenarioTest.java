package com.xebia.shop.rest;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xebia.shop.ShopApplication;
import com.xebia.shop.domain.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MvcResult;

import java.util.*;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ShopApplication.class)
@WebAppConfiguration
public class ScenarioTest extends TestBase {
    private ObjectMapper mapper = new ObjectMapper();

    private static Logger LOG = LoggerFactory.getLogger(ScenarioTest.class);

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Test
    public void createAccountAndOrderStuff() throws Exception {

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        WebUser webUser = createWebUser(objectMapper);

        Account account = createAcccount(objectMapper, webUser);

        ShoppingCart cart = createShoppingCart(objectMapper, webUser);

        Product product = createProduct(objectMapper);

        cart = addProductToCart(objectMapper, cart, product);

        Orderr orderr = createOrder(objectMapper, webUser);

        addAccountToOrder(account, orderr);

        approveOrder(orderr);

    }

    private void approveOrder(Orderr orderr) throws Exception {
        mockMvc.perform(put("/shop/orders/" + orderr.getUuid() + "/approve")
                .contentType(jsonContentType))
                .andExpect(status().isOk())
        ;
    }

    private void addAccountToOrder(Account account, Orderr orderr) throws Exception {
        mockMvc.perform(put("/shop/orders/" + orderr.getUuid() + "/account")
                .content(this.json(account))
                .contentType(jsonContentType))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.account.address", is(account.getAddress())))
        ;
    }

    private Orderr createOrder(ObjectMapper objectMapper, WebUser webUser) throws Exception {
        MvcResult resultActions;
        resultActions = mockMvc.perform(post("/shop/orders/add")
                .content(this.json(webUser.getUuid()))
                .contentType(jsonContentType))
                .andExpect(status().isCreated())
                .andReturn()
        ;

        String data = resultActions.getResponse().getContentAsString();
        LOG.info(data);
        return objectMapper.readValue(data, Orderr.class);
    }

    private WebUser getWebUser(ObjectMapper objectMapper, WebUser webUser) throws Exception {
        MvcResult resultActions;
        resultActions = mockMvc.perform(get("/shop/user/" + webUser.getUuid())
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

        resultActions = mockMvc.perform(post("/shop/cart/" + cart.getUuid() + "/add")
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
        MvcResult resultActions;
        ProductResource productResource = new ProductResource(UUID.randomUUID(), "product1", 10.0);
        resultActions = mockMvc.perform(post("/shop/products/")
                .content(this.json(productResource))
                .contentType(jsonContentType))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is("product1")))
                .andReturn()
        ;

        String data = resultActions.getResponse().getContentAsString();
        return objectMapper.readValue(data, Product.class);
    }

    private ShoppingCart createShoppingCart(ObjectMapper objectMapper, WebUser webUser) throws Exception {
        MvcResult resultActions;
        resultActions = mockMvc.perform(post("/shop/cart/user/" + webUser.getUuid()))
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
        resultActions = mockMvc.perform(post("/shop/accounts/user/" + webUser.getUuid())
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
        Random rnd = new Random(10);
        MvcResult resultActions;
        WebUserResource webUserResource = new WebUserResource("webuser" + rnd.toString(), "password");
        resultActions = mockMvc.perform(post("/shop/users/register")
                .content(this.json(webUserResource))
                .contentType(jsonContentType))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username", is("webuser" + rnd.toString()))).andReturn();

        String data = resultActions.getResponse().getContentAsString();
        return objectMapper.readValue(data, WebUser.class);
    }
}