package com.xebia.shop.rest.com.xebia.shop2.rest;


import com.xebia.shop.v2.Config;
import com.xebia.shop.v2.ShopApplication;
import com.xebia.shop.v2.domain.Clerk;
import com.xebia.shop.v2.domain.Orderr;
import com.xebia.shop.v2.domain.ShoppingCart;
import com.xebia.shop.v2.domain.WebUser;
import com.xebia.shop.v2.events.EventListener;
import com.xebia.shop.v2.repositories.OrderRepository;
import com.xebia.shop.v2.rest.OrderController;
import com.xebia.shop.v2.rest.OrderResource;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.IOException;
import java.util.UUID;

import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ShopApplication.class)
@WebAppConfiguration
public class OrderrTest extends TestBase {

    @Autowired
    EventListener eventListener;

    @Autowired
    @InjectMocks
    OrderController orderrController;

    @Autowired
    OrderRepository orderRepository;

    @Mock
    RabbitTemplate rabbitTemplate;

    @Before
    public void setup() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(orderrController).build();
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void approveOrder() throws Exception {
        Orderr orderr = createOrderr();
        orderr.setShippingAddress("shippingAddress");
        OrderResource orderResource = new OrderResource(orderr);
        mockMvc.perform(put("/shop/orders/")
                .content(this.json(orderResource))
                .contentType(jsonContentType))
                .andExpect(status().is2xxSuccessful())
                .andReturn()
        ;

        Mockito.doNothing().when(rabbitTemplate).convertAndSend(anyString(), anyString(), anyString());
        MvcResult resultActions = mockMvc.perform(put("/shop/orders/" + orderr.getUuid() + "/approve"))
                .andExpect(status().is2xxSuccessful())
                .andReturn()
        ;
        verify(rabbitTemplate, times(1)).convertAndSend(eq(Config.shopExchange), eq(Config.orderCompleted), anyString());
    }

    private Orderr createOrderr() throws IOException {
        Clerk clerk = new Clerk(new WebUser(UUID.randomUUID(), "username", "password"), UUID.randomUUID());
        ShoppingCart cart = eventListener.createCart(clerk);
        Orderr orderr = new Orderr(cart);
        orderr = orderRepository.save(orderr);
        return orderr;
    }
}