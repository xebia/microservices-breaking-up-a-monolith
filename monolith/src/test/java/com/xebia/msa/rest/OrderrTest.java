package com.xebia.msa.rest;


import com.xebia.msa.Application;
import com.xebia.msa.domain.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
public class OrderrTest extends TestBase {

    @Test
    public void testAddingAccountToOrder() throws Exception {
        Orderr orderr = createAndSaveOrderFromShoppingCart();
        Account account = createAndSaveAccount();
        mockMvc.perform(put("/monolith/orders/" + orderr.getUuid() + "/account")
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
        mockMvc.perform(put("/monolith/orders/" + orderr.getUuid() + "/pay")
                .contentType(jsonContentType))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.payment.total", is(10.0)))
        ;
    }

    @Test
    public void doNotApproveWithoutPayment() throws Exception {
        Orderr orderr = createAndSaveOrderFromShoppingCart();
        orderRepository.save(orderr);
        mockMvc.perform(put("/monolith/orders/" + orderr.getUuid() + "/approve")
                .contentType(jsonContentType))
                .andExpect(status().isForbidden())
        ;
    }

    @Test
    public void approveWhenPaid() throws Exception {
        Orderr orderr = createAndSaveOrderFromShoppingCart();
        Payment payment = createAndSavePayment();
        orderr.setPayment(payment);
        Account account = createAndSaveAccount();
        orderr.setAccount(account);
        orderRepository.save(orderr);
        mockMvc.perform(put("/monolith/orders/" + orderr.getUuid() + "/approve")
                .contentType(jsonContentType))
                .andExpect(status().isOk())
        ;
    }

    @Test
    public void shipmentCreatedWhenApproved() throws Exception {
        Orderr orderr = createAndSaveOrderFromShoppingCart();
        Payment payment = createAndSavePayment();
        orderr.setPayment(payment);
        Account account = createAndSaveAccount();
        orderr.setAccount(account);
        orderRepository.save(orderr);
        mockMvc.perform(put("/monolith/orders/" + orderr.getUuid() + "/approve")
                .contentType(jsonContentType))
                .andExpect(status().isOk())
        ;
        mockMvc.perform(get("/monolith/shipment/getByOrder/" + orderr.getUuid())
                .contentType(jsonContentType))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.address", is(orderr.getShippingAddress())))
                ;
    }
}