package com.xebia.shopmanager.rest;

import com.xebia.shopmanager.ShopManagerApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ShopManagerApplication.class)
@WebAppConfiguration
public class WebUserTest extends TestBase {

    @Mock
    RabbitTemplate rabbitTemplate;

    @Test
    public void testRegisterANewWebUser() throws Exception {
        WebUserResource webUserResource = new WebUserResource("x", "x");
        mockMvc.perform(post("/shop/users/register")
                .content(this.json(webUserResource))
                .contentType(jsonContentType))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username", is("x")))
        ;
    }
}
