package com.xebia.shop.rest.com.xebia.shop.v2.rest;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xebia.shop.v2.Application;
import com.xebia.shop.v2.domain.*;
import com.xebia.shop.v2.rest.NewLineItemResource;
import com.xebia.shop.v2.rest.WebUserResource;
import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.client.RestTemplate;

import java.util.Random;
import java.util.UUID;

import static junit.framework.Assert.assertEquals;
import static junit.framework.TestCase.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
public class ScenarioTest {
    public static final String PROTOCOL_AND_HOST = "http://192.168.99.100";
    public static final String PAY_ENDPOINT = PROTOCOL_AND_HOST + ":9001/payment";
    public static final String SHOP_ENDPOINT = PROTOCOL_AND_HOST + ":9002/shop/v2";
    public static final String FF_ENDPOINT = PROTOCOL_AND_HOST + ":9003/fulfillment";
    public static final String SHOPMANAGER_ENDPOINT = PROTOCOL_AND_HOST + ":9005/shop";
    private static Logger LOG = LoggerFactory.getLogger(ScenarioTest.class);

    Random rnd = new Random();

    @Test
    public void createAccountAndOrderStuff() throws Exception {

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        Product[] products = new Product[10];
        for (int i = 0; i < 10; i++) {
            products[i] = createProduct(objectMapper);
        }

        WebUser webUser = createWebUser(objectMapper);
        WebUser remoteUser = getWebUser(objectMapper, webUser);
        LOG.info("Read back user: " + remoteUser.toString());
        Clerk clerk = createClerk(objectMapper, webUser);
        waitASecond();

        ShoppingCart cart = getShoppingCart(objectMapper, clerk);
        addProductToCart(objectMapper, cart, products[1]);
        Orderr orderr = createOrderForWebUser(objectMapper, cart);
        orderr = setShippingAddress(objectMapper, orderr);
        approveOrder(orderr);

        waitASecond();

        Clerk clerk2 = findClerk(objectMapper, clerk);
        assertEquals(clerk.getUuid(), clerk2.getUuid());
        assertEquals(clerk.getOrderr(), clerk2.getOrderr());

//        sendPayment(objectMapper, orderr);

//        shipOrder(orderr.getUuid());

    }

    private Clerk findClerk(ObjectMapper objectMapper, Clerk clerk) throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> requestEntity = new HttpEntity<String>("{}", headers);
        ResponseEntity<String> responseEntity = restTemplate.exchange(SHOPMANAGER_ENDPOINT + "/session/" + clerk.getUuid(), HttpMethod.GET, requestEntity, String.class);
        String data = responseEntity.getBody().toString();
        return objectMapper.readValue(data, Clerk.class);
    }

    private void shipOrder(UUID orderID) throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> requestEntity = new HttpEntity<String>("{}", headers);
        ResponseEntity<String> responseEntity = restTemplate.exchange(FF_ENDPOINT + "/shipIt/" + orderID, HttpMethod.PUT, requestEntity, String.class);
        LOG.info("Orderr: " + orderID + " was shipped");
    }

    /* TODO: Fix after refactoring payment service
    private PaymentResource sendPayment(ObjectMapper objectMapper, Orderr orderr) throws Exception {
        LOG.info("Attempting to complete Payment for orderr: " + orderr.getUuid());
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> requestEntity = new HttpEntity<String>("", headers);
        Map<String, String> params = new HashMap<String, String>();
        params.put("orderId", orderr.getUuid().toString());
        ResponseEntity<String> responseEntity = restTemplate.exchange(PAY_ENDPOINT + "/pay/{orderId}/creditcard/1234", HttpMethod.PUT, requestEntity, String.class, params);
        String data = responseEntity.getBody().toString();
        PaymentResource paymentResource = objectMapper.readValue(data, PaymentResource.class);
        LOG.info("Payment: " + paymentResource.getUuid() + " has been paid.");
        return paymentResource;
    }
    */


    private void approveOrder(Orderr orderr) throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> requestEntity = new HttpEntity<String>("{}", headers);
        ResponseEntity<String> responseEntity = restTemplate.exchange(SHOP_ENDPOINT + "/orders/" + orderr.getUuid() + "/approve", HttpMethod.PUT, requestEntity, String.class);
        LOG.info("Orderr: " + orderr.getUuid() + " was approved");
    }

    private Orderr setShippingAddress(ObjectMapper objectMapper, Orderr orderr) throws Exception {
        orderr.setShippingAddress("shippingAddress");
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> requestEntity = new HttpEntity<String>(objectMapper.writeValueAsString(orderr), headers);
        headers.setContentType(MediaType.APPLICATION_JSON);
        ResponseEntity<String> responseEntity = restTemplate.exchange(SHOP_ENDPOINT + "/orders/", HttpMethod.PUT, requestEntity, String.class);
        LOG.info("Orderr: " + orderr.getUuid() + " ");
// test result?
        return orderr;
    }

    private Orderr createOrderForWebUser(ObjectMapper objectMapper, ShoppingCart cart) throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> requestEntity = new HttpEntity<String>("{}", headers);
        ResponseEntity<String> responseEntity = restTemplate.exchange(SHOP_ENDPOINT + "/cart/" + cart.getUuid() + "/order", HttpMethod.POST, requestEntity, String.class);
        String data = responseEntity.getBody().toString();
        Orderr orderr = objectMapper.readValue(data, Orderr.class);
        LOG.info("Created Orderr: " + orderr.getUuid());
        return orderr;
    }

    private WebUser getWebUser(ObjectMapper objectMapper, WebUser webUser) throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> requestEntity = new HttpEntity<String>("{}", headers);
        ResponseEntity<String> responseEntity = restTemplate.exchange(SHOPMANAGER_ENDPOINT + "/users/" + webUser.getUuid(), HttpMethod.GET, requestEntity, String.class);
        String data = responseEntity.getBody().toString();
        return objectMapper.readValue(data, WebUser.class);
    }

    private ShoppingCart addProductToCart(ObjectMapper objectMapper, ShoppingCart cart, Product product) throws Exception {
        LOG.info("Adding product: " + product.getUuid() + " to cart: " + cart.getUuid());
        NewLineItemResource lineItem = new NewLineItemResource(product.getUuid(), rnd.nextInt(10) + 1);
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> requestEntity = new HttpEntity<String>(objectMapper.writeValueAsString(lineItem), headers);
        ResponseEntity<String> responseEntity = restTemplate.exchange(SHOP_ENDPOINT + "/cart/" + cart.getUuid() + "/add", HttpMethod.POST, requestEntity, String.class);
        String data = responseEntity.getBody().toString();
        cart = objectMapper.readValue(data, ShoppingCart.class);
        return cart;
    }

    private Product createProduct(ObjectMapper objectMapper) throws Exception {
        Product product = new Product(UUID.randomUUID(), "product" + rnd.nextInt(1000), "supplier", rnd.nextDouble() * 100);
        LOG.info("Created product:" + product.toString());
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> requestEntity = new HttpEntity<String>(objectMapper.writeValueAsString(product), headers);
        ResponseEntity<String> responseEntity = restTemplate.exchange(SHOP_ENDPOINT + "/products/", HttpMethod.POST, requestEntity, String.class);
        String data = responseEntity.getBody().toString();
        return objectMapper.readValue(data, Product.class);
    }

    private ShoppingCart getShoppingCart(ObjectMapper objectMapper, Clerk clerk) throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> requestEntity = new HttpEntity<String>("{}", headers);
        ResponseEntity<String> responseEntity = restTemplate.exchange(SHOP_ENDPOINT + "/cart/forClerk/" + clerk.getUuid(), HttpMethod.GET, requestEntity, String.class);
        String data = responseEntity.getBody().toString();
        ShoppingCart cart = objectMapper.readValue(data, ShoppingCart.class);
        assertNotNull(cart.getUuid());
        return cart;
    }

    private Clerk createClerk(ObjectMapper objectMapper, WebUser webUser) throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> requestEntity = new HttpEntity<String>("{}", headers);
        ResponseEntity<String> responseEntity = restTemplate.exchange(SHOPMANAGER_ENDPOINT + "/session/" + webUser.getUuid(), HttpMethod.POST, requestEntity, String.class);
        String data = responseEntity.getBody().toString();
        return objectMapper.readValue(data, Clerk.class);
    }

    private WebUser createWebUser(ObjectMapper objectMapper) throws Exception {
        WebUserResource webUserResource = new WebUserResource("webuser" + UUID.randomUUID(), "password");
        LOG.info("Created user:" + webUserResource.getUsername());
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> requestEntity = new HttpEntity<String>(objectMapper.writeValueAsString(webUserResource), headers);
        ResponseEntity<String> responseEntity = restTemplate.exchange(SHOPMANAGER_ENDPOINT + "/users/register", HttpMethod.POST, requestEntity, String.class);
        String data = responseEntity.getBody().toString();
        return objectMapper.readValue(data, WebUser.class);
    }

    private void waitASecond() throws InterruptedException {
        Thread.sleep(1000);
    }
}

