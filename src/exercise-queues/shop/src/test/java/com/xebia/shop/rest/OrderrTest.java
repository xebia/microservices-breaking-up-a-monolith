package com.xebia.shop.rest;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.xebia.shop.ShopApplication;
import com.xebia.shop.domain.*;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ShopApplication.class)
@WebAppConfiguration
public class OrderrTest extends TestBase {

    @Test
    public void testAddingAccountToOrder() throws Exception {
        Orderr orderr = createAndSaveOrderFromShoppingCart();
        Account account = createAndSaveAccount();
        mockMvc.perform(put("/cart/orders/" + orderr.getUuid() + "/account")
                .content(this.json(account))
                .contentType(jsonContentType))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.account.address", is(account.getAddress())))
        ;
    }

    @Test
    public void pay() throws Exception {
        Orderr orderr = createAndSaveOrderFromShoppingCart();
        orderRepository.save(orderr);
        mockMvc.perform(put("/cart/orders/registerPayment/" + orderr.getUuid())
                .contentType(jsonContentType))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paymentReceived", is(true)))
        ;
    }

}