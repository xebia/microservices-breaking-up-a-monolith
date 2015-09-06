package com.xebia.catalog.rest;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.xebia.catalog.Application;
import com.xebia.catalog.domain.Product;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MvcResult;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
public class FeedTest extends TestBase {

    @Before
    public void setup() throws Exception {
        this.mockMvc = webAppContextSetup(webApplicationContext).build();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }


    @Test
    public void testFormattingAsRSSFeed() {
        List<Product> products = new ArrayList<>();
        products.add(new Product("p1", "s1", 1.0));
        products.add(new Product("p2", "s2", 1.0));
        try {
            String feed = new ProductController().asRssFeed(products);
            assertTrue(feed.contains("<name>p1</name>"));
            assertTrue(feed.contains("<name>p2</name>"));
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testFeed() throws Exception {
        Product product = new Product(UUID.randomUUID(), "product1", "supplier1", 10.0);
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

        MvcResult resultActions = mockMvc.perform(get("/product/rss").header("If-Modified-Since", date))
                .andExpect(status().isOk())
                .andReturn();

        String data = resultActions.getResponse().getContentAsString();
        assertTrue(data.contains("<name>product5</name>"));
        assertFalse(data.contains("<name>product1</name>"));
    }
}