package com.xebia.shop.rest;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.xebia.shop.ShopApplication;
import com.xebia.shop.domain.*;

import java.util.ArrayList;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ShopApplication.class)
@WebAppConfiguration
public class ShoppingCartTest extends TestBase {
    @Test
    public void userFound() throws Exception {
        WebUser user = createAndSaveWebUser();
        mockMvc.perform(get("/cart/user/" + user.getUuid().toString())
                .content(this.json(new WebUser()))
                .contentType(jsonContentType))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", is(user.getUsername())))
                .andExpect(jsonPath("$.account.phoneNumber", is(user.getAccount().getPhoneNumber())))
        ;
    }

    @Test
    public void allowOnlyOneShoppingCart() throws Exception {
        WebUser user = createAndSaveWebUserNoDetails();
        mockMvc.perform(post("/cart/user/" + user.getUuid() + "/cart"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.lineItems", is(new ArrayList<LineItem>())))
        ;
        mockMvc.perform(post("/cart/user/" + user.getUuid() + "/cart"))
                .andExpect(status().isNotAcceptable())
        ;
    }

    @Test
    public void orderShoppingCart() throws Exception {
        ShoppingCart cart = createAndSaveShoppingCart();
        mockMvc.perform(post("/cart/cart/" + cart.getUuid() + "/order")
                .contentType(jsonContentType))
                .andExpect(status().isCreated())
        ;
    }

    @Test
    public void addProductToCart() throws Exception {
        ShoppingCart cart = createAndSaveShoppingCart();
        Product product = createAndSaveProduct();
        NewLineItemResource lineItem = new NewLineItemResource(product.getUuid(), 10);
        mockMvc.perform(post("/cart/cart/" + cart.getUuid() + "/add")
                .content(this.json(lineItem))
                .contentType(jsonContentType))
                .andExpect(status().isCreated())
        ;
    }

    @Test
    public void getCart() throws Exception {
        ShoppingCart cart = createAndSaveShoppingCart();
        WebUser user = createAndSaveWebUser(cart.getUuid());
        mockMvc.perform(get("/cart/user/" + user.getUuid() + "/cart")
                .contentType(jsonContentType))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.uuid", is(cart.getUuid().toString())))
        ;
    }
}