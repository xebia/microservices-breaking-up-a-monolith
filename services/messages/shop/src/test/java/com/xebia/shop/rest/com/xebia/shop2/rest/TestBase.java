package com.xebia.shop.rest.com.xebia.shop2.rest;


import com.xebia.shop.v2.ShopApplication;
import com.xebia.shop.v2.domain.*;
import com.xebia.shop.v2.repositories.*;
import org.junit.Assert;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.http.MockHttpOutputMessage;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Date;
import java.util.UUID;

import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@SpringApplicationConfiguration(classes = ShopApplication.class)
@WebAppConfiguration
public class TestBase {

    protected MediaType jsonContentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(),
            Charset.forName("utf8"));

    protected MediaType textType = new MediaType(MediaType.TEXT_PLAIN.getType());

    protected MockMvc mockMvc;

    protected HttpMessageConverter mappingJackson2HttpMessageConverter;

    @Autowired
    protected WebApplicationContext webApplicationContext;

    @Autowired
    protected WebUserRepository webUserRepository;
    @Autowired
    protected ShoppingCartRepository shoppingCartRepository;
    @Autowired
    protected OrderRepository orderRepository;
    @Autowired
    protected LineItemRepository lineItemRepository;
    @Autowired
    protected ProductRepository productRepository;

    @Autowired
    void setConverters(HttpMessageConverter<?>[] converters) {

        this.mappingJackson2HttpMessageConverter = Arrays.asList(converters).stream().filter(
                hmc -> hmc instanceof MappingJackson2HttpMessageConverter).findAny().get();

        Assert.assertNotNull("the JSON message converter must not be null",
                this.mappingJackson2HttpMessageConverter);
    }

    protected WebUser createAndSaveWebUser() {
        UUID webUserUuid = UUID.randomUUID();
        String id = webUserUuid.toString().substring(1, 5);
        WebUser user = new WebUser(webUserUuid, "user"+id, "password");
        return webUserRepository.save(user);
    }

    protected WebUser createAndSaveWebUser(UUID cartId) {
        UUID webUserUuid = UUID.randomUUID();
        String id = webUserUuid.toString().substring(1, 5);
        WebUser user = new WebUser(webUserUuid, "user"+id, "password");
        shoppingCartRepository.findOne(cartId);
        return webUserRepository.save(user);
    }

    protected WebUser createAndSaveWebUserNoDetails() {
        UUID webUserUuid = UUID.randomUUID();
        String id = webUserUuid.toString().substring(1, 5);
        WebUser user = new WebUser(webUserUuid, "user"+id, "password");
        return webUserRepository.save(user);
    }

    protected Product createAndSaveProduct() {
        UUID productUuid = UUID.randomUUID();
        String id = productUuid.toString().substring(1, 5);
        Product product = new Product(productUuid, "product" + id, "supplier" + id, new Double(112.10));
        return productRepository.save(product);
    }

    protected ShoppingCart createAndSaveShoppingCart() {
        UUID shoppingCartUuid = UUID.randomUUID();
        ShoppingCart cart = new ShoppingCart(new Date(), shoppingCartUuid);
        cart.addLineItem(createAndSaveLineItem());
        return shoppingCartRepository.save(cart);
    }

    protected LineItem createAndSaveLineItem() {
        UUID lineItem1Uuid = UUID.randomUUID();
        LineItem item1 = new LineItem(lineItem1Uuid, 1, 10, createAndSaveProduct());
        return lineItemRepository.save(item1);
    }

    protected Orderr createAndSaveOrderFromShoppingCart() {
        Orderr order = new Orderr(createAndSaveShoppingCart());
        return orderRepository.save(order);
    }

    protected Orderr createAndSaveOrderFromShoppingCart(ShoppingCart cart) {
        Orderr order = new Orderr(cart);
        return orderRepository.save(order);
    }

    @Before
    public void setup() throws Exception {
        this.mockMvc = webAppContextSetup(webApplicationContext).build();
    }

   protected String json(Object o) throws IOException {
        MockHttpOutputMessage mockHttpOutputMessage = new MockHttpOutputMessage();
        this.mappingJackson2HttpMessageConverter.write(
                o, MediaType.APPLICATION_JSON, mockHttpOutputMessage);
        return mockHttpOutputMessage.getBodyAsString();
    }
}