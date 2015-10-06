package com.xebia.shop.rest;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.xebia.shop.ShopApplication;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ShopApplication.class)
@WebAppConfiguration
public class WebUserTest extends TestBase {
    @Test
    public void registerANewWebUser() throws Exception {
        WebUserResource webUserResource = new WebUserResource("x", "x");
        mockMvc.perform(post("/cart/user/register" )
                .content(this.json(webUserResource))
                .contentType(jsonContentType))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username", is("x")))
        ;
    }
}
