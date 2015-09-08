package com.xebia.msa.rest;


import com.xebia.msa.Application;
import com.xebia.msa.domain.Account;
import com.xebia.msa.domain.Orderr;
import com.xebia.msa.domain.Payment;
import com.xebia.msa.domain.Shipment;
import com.xebia.msa.repositories.ShipmentRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
public class ShipmentTest extends TestBase {

    @Autowired
    private ShipmentRepository shipmentRepository;

    @Test
    public void testShipmentHasStatusShippableWhenCreated() throws Exception {
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
                .andExpect(jsonPath("$.status", is(Shipment.SHIPPABLE)))
                ;
    }

    @Test
    public void testShipmentBecomesShippableWhenShipItMethodIsCalled() throws Exception {
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
        Shipment shipment = shipmentRepository.findByOrderr(orderr);
        mockMvc.perform(put("/monolith/shipment/shipIt/" + shipment.getUuid())
                .contentType(jsonContentType))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(Shipment.SHIPPED)))
        ;


    }

}