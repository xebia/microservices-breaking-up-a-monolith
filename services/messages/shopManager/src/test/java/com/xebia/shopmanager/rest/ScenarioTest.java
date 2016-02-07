package com.xebia.shopmanager.rest;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xebia.shopmanager.ShopManagerApplication;
import com.xebia.shopmanager.domain.Clerk;
import com.xebia.shopmanager.domain.Orderr;
import com.xebia.shopmanager.domain.WebUser;
import com.xebia.shopmanager.repositories.ClerkRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Random;
import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ShopManagerApplication.class)
@WebAppConfiguration
public class ScenarioTest extends TestBase {

    private ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    @InjectMocks
    ClerkController clerkController;

    @Mock
    RabbitTemplate rabbitTemplate;

    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);
        this.mockMvc = webAppContextSetup(webApplicationContext).build();
        ignoreUnknownJsonProperties();
    }

    private void ignoreUnknownJsonProperties() {
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Autowired
    ClerkRepository clerkRepository;

    @Test
    public void testScenario () {
        try {
            WebUser webUser = createWebUser(objectMapper);
            MvcResult result = mockMvc.perform(post("/shop/session/" + webUser.getUuid()))
                    .andExpect(status().isCreated())
                    .andReturn()
            ;
            String data = result.getResponse().getContentAsString();
            Clerk clerk = objectMapper.readValue(data, Clerk.class);
            Orderr orderr = new Orderr(UUID.randomUUID(), new java.util.Date(), "address", "status");
            clerk.setOrderr(orderr);
            clerkRepository.save(clerk);

            result = mockMvc.perform(get("/shop/session/" + clerk.getUuid()))
                    .andReturn()
                    ;
            data = result.getResponse().getContentAsString();
            Clerk clerk2 = objectMapper.readValue(data, Clerk.class);
            assertEquals(clerk.getOrderr().getUuid(), clerk2.getOrderr().getUuid());

        } catch (Exception e) {
            fail ("Exception occurred " + e.getMessage());
        }
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
