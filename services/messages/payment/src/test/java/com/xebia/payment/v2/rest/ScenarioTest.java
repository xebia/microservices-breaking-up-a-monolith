package com.xebia.payment.v2.rest;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xebia.payment.v2.PaymentApplication;
import com.xebia.payment.v2.domain.Clerk;
import com.xebia.payment.v2.domain.Payment;
import com.xebia.payment.v2.domain.ShoppingCart;
import com.xebia.payment.v2.domain.WebUser;
import com.xebia.payment.v2.events.EventListener;
import org.hibernate.service.spi.InjectService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.http.MockHttpOutputMessage;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = PaymentApplication.class)
@WebAppConfiguration
public class ScenarioTest {

    @Autowired
    protected WebApplicationContext webApplicationContext;

    @Autowired
    protected EventListener eventListener;

    @Mock
    RabbitTemplate rabbitTemplate;

    @InjectMocks
    @Autowired
    PaymentController paymentController;

    protected MediaType textType = new MediaType(MediaType.TEXT_PLAIN.getType());

    protected MockMvc mockMvc;

    ObjectMapper objectMapper = new ObjectMapper();

    protected HttpMessageConverter mappingJackson2HttpMessageConverter;

    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);
        this.mockMvc = webAppContextSetup(webApplicationContext).build();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Test
    public void payForOrder() throws Exception {
        Clerk clerk = new Clerk(new WebUser(UUID.randomUUID(), "username", "password"), UUID.randomUUID());
        clerk.setShoppingCart(new ShoppingCart(new Date(), UUID.randomUUID()));
        Payment payment = eventListener.createPayment(clerk);
        MvcResult resultActions;
        resultActions = mockMvc.perform(put("/payment/v2/pay/" + payment.getUuid() + "/creditcard/c123")
                .contentType(jsonContentType))
                .andExpect(status().isOk())
                .andReturn()
        ;
        String data = resultActions.getResponse().getContentAsString();
        Payment newPayment = objectMapper.readValue(data, Payment.class);
        assertEquals("c123", newPayment.getCardId());

        resultActions = mockMvc.perform(get("/payment/v2/forClerk/" + clerk.getUuid())).andReturn();
        data = resultActions.getResponse().getContentAsString();
        Payment payment3 = objectMapper.readValue(data, Payment.class);
        assertEquals(payment3, newPayment);

    }

    protected String json(Object o) throws IOException {
        MockHttpOutputMessage mockHttpOutputMessage = new MockHttpOutputMessage();
        this.mappingJackson2HttpMessageConverter.write(
                o, MediaType.APPLICATION_JSON, mockHttpOutputMessage);
        return mockHttpOutputMessage.getBodyAsString();
    }

    @Autowired
    void setConverters(HttpMessageConverter<?>[] converters) {

        this.mappingJackson2HttpMessageConverter = Arrays.asList(converters).stream().filter(
                hmc -> hmc instanceof MappingJackson2HttpMessageConverter).findAny().get();

        org.junit.Assert.assertNotNull("the JSON message converter must not be null",
                this.mappingJackson2HttpMessageConverter);
    }

    protected MediaType jsonContentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(),
            Charset.forName("utf8"));

}
