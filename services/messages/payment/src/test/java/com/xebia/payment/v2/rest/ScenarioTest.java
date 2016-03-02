package com.xebia.payment.v2.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xebia.payment.v2.Config;
import com.xebia.payment.v2.PaymentApplication;
import com.xebia.payment.v2.domain.Clerk;
import com.xebia.payment.v2.domain.Document;
import com.xebia.payment.v2.domain.Payment;
import com.xebia.payment.v2.events.EventListener;
import com.xebia.payment.v2.repositories.ClerkRepository;
import com.xebia.payment.v2.repositories.DocumentRepository;
import com.xebia.payment.v2.repositories.PaymentRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.hateoas.alps.Doc;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.http.MockHttpOutputMessage;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Date;
import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

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

    @Autowired
    PaymentRepository paymentRepository;

    @Autowired
    DocumentRepository documentRepository;

    @Autowired
    ClerkRepository clerkRepository;

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
    public void payForOrderTest() throws Exception {
        File file = new File(getClass().getClassLoader().getResource("basicDocument.json").getFile());
        Document document = new Document(file);
        Clerk clerk = document.getClerk();
        clerk.setUuid(UUID.randomUUID());
        document.setClerk(clerk);
        Payment payment = eventListener.createPayment(document);
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

    @Test
    public void testDocumentAndNotAClerkIsSentOnQueue() throws Exception {
        Mockito.doNothing().when(rabbitTemplate).convertAndSend(anyString(), anyString(), anyString());
        ArgumentCaptor<String> argument = ArgumentCaptor.forClass(String.class);
        File file = new File(getClass().getClassLoader().getResource("clerk.json").getFile());
        Document document = new Document(file);
        documentRepository.save(document);
        Payment payment = new Payment(UUID.randomUUID());
        paymentRepository.save(payment);
        Clerk clerk = document.getClerk();
        clerk.setPayment(payment);
        clerkRepository.save(clerk);
        paymentController.updateDocument(payment.getUuid(), "c123");
        verify(rabbitTemplate, times(1)).convertAndSend(eq(Config.shopExchange), anyString(), argument.capture());
        // TODO: why is the status SHIPPED?
        assertTrue(argument.getValue().indexOf("\"status\":\"SHIPPED\"")>0);
    }

    @Test
    public void findDocumentByClerkTest() throws Exception{
        File file = new File(getClass().getClassLoader().getResource("clerk.json").getFile());
        Document document = new Document(file);
        document.setUuid(UUID.randomUUID());
        Clerk clerk = document.getClerk();
        clerk.setUuid(UUID.randomUUID());
        document.setClerk(clerk);
        documentRepository.save(document);
        Document document2 = documentRepository.findByClerkUuid(document.getClerk().getUuid());
        assertEquals(document2.getUuid(), document.getUuid());
    }

    protected String json(Object o) throws IOException {
        MockHttpOutputMessage mockHttpOutputMessage = new MockHttpOutputMessage();
        this.mappingJackson2HttpMessageConverter.write(
                o, MediaType.APPLICATION_JSON, mockHttpOutputMessage);
        return mockHttpOutputMessage.getBodyAsString();
    }

    @Test
    public void testFindPaymentByClerkReturnsTheCorrectPayment() throws Exception {
        Clerk clerk1 = new Clerk(UUID.randomUUID(), 1);
        Payment payment1 = new Payment(UUID.randomUUID());
        clerk1.setPayment(payment1);
        clerkRepository.save(clerk1);
        Clerk clerk2 = new Clerk(UUID.randomUUID(), 2);
        Payment payment2 = new Payment(UUID.randomUUID(), new Date(), 1.0, "desc", "c123");
        clerk2.setPayment(payment2);
        clerkRepository.save(clerk2);
        MvcResult resultActions;
        resultActions = mockMvc.perform(get("/payment/v2/forClerk/" + clerk2.getUuid())
                .contentType(jsonContentType))
                .andExpect(status().isOk())
                .andReturn()
        ;
        String data = resultActions.getResponse().getContentAsString();
        Payment newPayment = objectMapper.readValue(data, Payment.class);
        assertEquals(newPayment.asJson(), payment2.asJson());
    }

    @Test
    public void testFindPaymentReturnsTheCorrectPayment() throws Exception {
        Clerk clerk1 = new Clerk(UUID.randomUUID(), 1);
        Payment payment1 = new Payment(UUID.randomUUID());
        clerk1.setPayment(payment1);
        clerkRepository.save(clerk1);
        Clerk clerk2 = new Clerk(UUID.randomUUID(), 2);
        Payment payment2 = new Payment(UUID.randomUUID(), new Date(), 1.0, "desc", "c123");
        clerk2.setPayment(payment2);
        clerkRepository.save(clerk2);
        MvcResult resultActions;
        resultActions = mockMvc.perform(get("/payment/v2/" + payment2.getUuid())
                .contentType(jsonContentType))
                .andExpect(status().isOk())
                .andReturn()
        ;
        String data = resultActions.getResponse().getContentAsString();
        Payment newPayment = objectMapper.readValue(data, Payment.class);
        assertEquals(newPayment.asJson(), payment2.asJson());
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
