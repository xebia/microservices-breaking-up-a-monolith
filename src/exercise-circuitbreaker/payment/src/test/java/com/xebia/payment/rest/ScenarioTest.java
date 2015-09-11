package com.xebia.payment.rest;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xebia.payment.PaymentApplication;
import com.xebia.payment.domain.Payment;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.HttpHeaders;
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

    protected MediaType jsonContentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(),
            Charset.forName("utf8"));

    protected MediaType textType = new MediaType(MediaType.TEXT_PLAIN.getType());

    protected MockMvc mockMvc;

    ObjectMapper objectMapper = new ObjectMapper();

    protected HttpMessageConverter mappingJackson2HttpMessageConverter;

    @Before
    public void setup() throws Exception {
        this.mockMvc = webAppContextSetup(webApplicationContext).build();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Test
    public void payForOrder() throws Exception {
        UUID id = UUID.randomUUID();
        OrderrResource orderr = new OrderrResource(id, 12.0, "description100");
        MvcResult resultActions = mockMvc.perform(post("/payment")
                .content(json(orderr))
                .contentType(jsonContentType))
                .andExpect(status().isCreated())
                .andReturn()
                ;
        String data = resultActions.getResponse().getContentAsString().trim();

        HttpHeaders headers = new HttpHeaders();
        headers.add("registerurl","");
        resultActions = mockMvc.perform(put("/payment/pay/" + UUID.fromString(data) + "/creditcard/c123")
                .headers(headers)
                .contentType(jsonContentType))
                .andExpect(status().isOk())
                .andReturn()
        ;
        data = resultActions.getResponse().getContentAsString();
        Payment payment = objectMapper.readValue(data, Payment.class);
        assertEquals("c123", payment.getCardId());

    }

    @Test
    public void listAllPayments() throws  Exception {
        registerOrderAndPay("description");
        registerOrderAndPay("description2");

        MvcResult resultActions = mockMvc.perform(get("/payment/all")
                .contentType(jsonContentType))
                .andExpect(status().isOk())
                .andReturn()
                ;
        String data = resultActions.getResponse().getContentAsString();
        List<Payment> payments = objectMapper.readValue(data, new TypeReference<List<Payment>>() {
        });
        boolean found = false;
        for (Payment p:payments) {
            if (p.getDescription().equals("description2")) {found = true; break;}
        }
        assertTrue(found);
    }

    @Test
    public void listPaymentsSince() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        registerOrderAndPay("description");

        waitAWhile(1000);
        String date = getTimeStamp();
        waitAWhile(1000);

        registerOrderAndPay("description2");

        MvcResult resultActions = mockMvc.perform(get("/payment/list").header("If-Modified-Since",date)
                .contentType(jsonContentType))
                .andExpect(status().isOk())
                .andReturn()
                ;
        String data = resultActions.getResponse().getContentAsString();
        List<Payment> payments = objectMapper.readValue(data, new TypeReference<List<Payment>>() {
        });
        boolean found = false;
        for (Payment p:payments) {
            if (p.getDescription().equals("description2")) {found = true; break;}
        }
        assertTrue(found);
        found = false;
        for (Payment p:payments) {
            if (p.getCardId().equals("description1")) {found = true; break;}
        }
        assertFalse(found);
    }

    private String getTimeStamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z");
        Date cutoffDate = new Date();
        return sdf.format(cutoffDate);
    }

    private void registerOrderAndPay(String description) throws Exception {
        UUID orderUuid = UUID.randomUUID();
        OrderrResource orderr = new OrderrResource(orderUuid, 10.0, description);
        MvcResult resultActions = mockMvc.perform(post("/payment/")
                .content(this.json(orderr))
                .contentType(jsonContentType))
                .andExpect(status().isCreated())
                .andReturn()
                ;
        String data = resultActions.getResponse().getContentAsString();

        HttpHeaders headers = new HttpHeaders();
        headers.add("registerurl","");
        mockMvc.perform(put("/payment/pay/" + UUID.fromString(data) + "/creditcard/c123")
                .headers(headers)
                .contentType(jsonContentType))
                .andExpect(status().isOk())
        ;

    }

    private void waitAWhile(long intervalInMs) {
        try {
            Thread.sleep(intervalInMs);
        } catch (Exception e) {
            fail("Exception in testModifiedSinceReturnsOnlyNewPayments");
        }
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


}
