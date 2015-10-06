package com.xebia.shop.rest;


import com.xebia.shop.ShopApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.xebia.shop.domain.Account;
import com.xebia.shop.domain.WebUser;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ShopApplication.class)
@WebAppConfiguration
public class AccountTest extends TestBase {
    @Test
    public void createAccountForWebUser() throws Exception {
        WebUser user = createAndSaveWebUserNoDetails();
        mockMvc.perform(get("/shop/users/" + user.getUuid().toString())
                .content(this.json(new WebUser()))
                .contentType(jsonContentType))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", is(user.getUsername())))
                .andExpect(jsonPath("$.account", isEmptyOrNullString()))
        ;
        Account account = createAccount();
        WebUser user2 = createAndSaveWebUserNoDetails();
        mockMvc.perform(post("/shop/accounts/user/" + user2.getUuid())
                .contentType(jsonContentType)
                .content(json(account)))
                .andExpect(status().isCreated())
        ;
        mockMvc.perform(get("/shop/users/" + user2.getUuid())
                .content(this.json(new WebUser()))
                .contentType(jsonContentType))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", is(user2.getUsername())))
                .andExpect(jsonPath("$.account.address", is(account.getAddress())))
        ;
    }
}