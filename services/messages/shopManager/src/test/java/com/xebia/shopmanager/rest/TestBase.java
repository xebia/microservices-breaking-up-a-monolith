package com.xebia.shopmanager.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xebia.shopmanager.domain.WebUser;
import com.xebia.shopmanager.repositories.WebUserRepository;
import org.junit.Before;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.http.MockHttpOutputMessage;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.UUID;

import static org.junit.Assert.fail;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

public class TestBase {
    private static final Logger LOG = LoggerFactory.getLogger(TestBase.class);
    @Autowired
    protected WebApplicationContext webApplicationContext;
    protected MediaType jsonContentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(),
            Charset.forName("utf8"));
    protected MediaType textType = new MediaType(MediaType.TEXT_PLAIN.getType());
    protected MockMvc mockMvc;
    protected HttpMessageConverter mappingJackson2HttpMessageConverter;
    ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    protected WebUserRepository webUserRepository;

    protected String getTimeStamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z");
        Date cutoffDate = new Date();
        return sdf.format(cutoffDate);
    }

    protected void waitAWhile(long intervalInMs) {
        try {
            Thread.sleep(intervalInMs);
        } catch (Exception e) {
            LOG.debug(e.getMessage());
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

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        this.mockMvc = webAppContextSetup(webApplicationContext).build();
    }

    protected WebUser createAndSaveWebUserNoDetails() {
        UUID webUserUuid = UUID.randomUUID();
        String id = webUserUuid.toString().substring(1, 5);
        WebUser user = new WebUser(webUserUuid, "user"+id, "password");
        return webUserRepository.save(user);
    }
}
