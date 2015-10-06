package com.xebia.catalog.rest;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xebia.catalog.CatalogApplication;
import com.xebia.catalog.domain.Product;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MvcResult;

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
@SpringApplicationConfiguration(classes = CatalogApplication.class)
@WebAppConfiguration
public class ScenarioTest extends TestBase {

    @Before
    public void setup() throws Exception {
        this.mockMvc = webAppContextSetup(webApplicationContext).build();
    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Test
    public void addProduct() throws Exception {
        Product product = new Product(UUID.randomUUID(), "product1", "supplier1", 10.0);
        mockMvc.perform(post("/product/")
                .content(this.json(product))
                .contentType(jsonContentType))
                .andExpect(status().isCreated())
                .andReturn()
        ;
    }

    @Test
    public void listAllProducts() throws  Exception {
        Product product = new Product(UUID.randomUUID(), "product2", "supplier1", 10.0);
        mockMvc.perform(post("/product/")
                .content(this.json(product))
                .contentType(jsonContentType))
                .andExpect(status().isCreated())
                .andReturn()
        ;
        product = new Product(UUID.randomUUID(), "product3", "supplier1", 10.0);
        mockMvc.perform(post("/product/")
                .content(this.json(product))
                .contentType(jsonContentType))
                .andExpect(status().isCreated())
                .andReturn()
        ;

        MvcResult resultActions = mockMvc.perform(get("/product/all")
                .contentType(jsonContentType))
                .andExpect(status().isOk())
                .andReturn()
        ;
        String data = resultActions.getResponse().getContentAsString();
        List<Product> products = objectMapper.readValue(data, new TypeReference<List<Product>>() {
        });
        boolean found = false;
        for (Product p:products) {
            if (p.getName().equals("product2")) {found = true; break;}
        }
        assertTrue(found);
    }

    @Test
    public void listProductsSince() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        Product product = new Product(UUID.randomUUID(), "product4", "supplier1", 10.0);
        mockMvc.perform(post("/product/")
                .content(this.json(product))
                .contentType(jsonContentType))
                .andExpect(status().isCreated())
        ;

        waitAWhile(1000);
        String date = getTimeStamp();
        waitAWhile(1000);

        product = new Product(UUID.randomUUID(), "product5", "supplier1", 10.0);
        mockMvc.perform(post("/product/")
                .content(this.json(product))
                .contentType(jsonContentType))
                .andExpect(status().isCreated())
        ;

        MvcResult resultActions = mockMvc.perform(get("/product/list").header("If-Modified-Since",date)
                .contentType(jsonContentType))
                .andExpect(status().isOk())
                .andReturn()
        ;
        String data = resultActions.getResponse().getContentAsString();
        List<Product> products = objectMapper.readValue(data, new TypeReference<List<Product>>() {
        });
        boolean found = false;
        for (Product p:products) {
            if (p.getName().equals("product5")) {found = true; break;}
        }
        assertTrue(found);
        for (Product p:products) {
            if (p.getName().equals("product4")) {found = true; break;}
        }
        found = false;
        assertFalse(found);
    }
}
