package com.xebia.shop.rest;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xebia.shop.Application;
import com.xebia.shop.domain.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
public class ScenarioTest {
    public static final String PROTOCOL_AND_HOST = "http://192.168.99.100";
    public static final String PAY_ENDPOINT = PROTOCOL_AND_HOST + ":9001/payment";
    public static final String SHOP_ENDPOINT = PROTOCOL_AND_HOST + ":9002/shop";
    public static final String FF_ENDPOINT = PROTOCOL_AND_HOST + ":9003/fulfillment";
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

        Account account = createAcccount(objectMapper, webUser);
        ShoppingCart cart = createShoppingCart(objectMapper, webUser);
        addProductToCart(objectMapper, cart, products[1]);
        Orderr orderr = createOrderForWebUser(objectMapper, webUser);
        addAccountToOrder(objectMapper, account, orderr);
        approveOrder(orderr);

        waitForNewOrderToBeProcessed();

        sendPayment(objectMapper, orderr);

        shipOrder(orderr.getUuid());

    }

    private void shipOrder(UUID orderID) throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> requestEntity = new HttpEntity<String>("{}", headers);
        ResponseEntity<String> responseEntity = restTemplate.exchange(FF_ENDPOINT + "/shipIt/" + orderID, HttpMethod.PUT, requestEntity, String.class);
        LOG.info("Orderr: " + orderID + " was shipped");
    }

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

    private void approveOrder(Orderr orderr) throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> requestEntity = new HttpEntity<String>("{}", headers);
        ResponseEntity<String> responseEntity = restTemplate.exchange(SHOP_ENDPOINT + "/orders/" + orderr.getUuid() + "/approve", HttpMethod.PUT, requestEntity, String.class);
        LOG.info("Orderr: " + orderr.getUuid() + " was approved");
    }

    private void addAccountToOrder(ObjectMapper objectMapper, Account account, Orderr orderr) throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> requestEntity = new HttpEntity<String>(objectMapper.writeValueAsString(account), headers);
        ResponseEntity<String> responseEntity = restTemplate.exchange(SHOP_ENDPOINT + "/orders/" + orderr.getUuid() + "/account", HttpMethod.PUT, requestEntity, String.class);
        String data = responseEntity.getBody().toString();
        orderr = objectMapper.readValue(data, Orderr.class);

        LOG.info("Added Account: " + account.getUuid() + " to Orderr: " + orderr.getUuid());
    }

    private Orderr createOrderForWebUser(ObjectMapper objectMapper, WebUser webUser) throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> requestEntity = new HttpEntity<String>(objectMapper.writeValueAsString(webUser.getUuid()), headers);
        ResponseEntity<String> responseEntity = restTemplate.exchange(SHOP_ENDPOINT + "/orders/add", HttpMethod.POST, requestEntity, String.class);
        String data = responseEntity.getBody().toString();
        Orderr orderr = objectMapper.readValue(data, Orderr.class);

        LOG.info("Created Orderr: " + orderr.getUuid() + " from webUser: " + webUser.getUsername());

        return orderr;
    }

    private WebUser getWebUser(ObjectMapper objectMapper, WebUser webUser) throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> requestEntity = new HttpEntity<String>("{}", headers);
        ResponseEntity<String> responseEntity = restTemplate.exchange(SHOP_ENDPOINT + "/users/" + webUser.getUuid(), HttpMethod.GET, requestEntity, String.class);
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
        Product product = new Product(UUID.randomUUID(), "product" + rnd.nextInt(1000), rnd.nextDouble() * 100);

        LOG.info("Created product:" + product.toString());

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> requestEntity = new HttpEntity<String>(objectMapper.writeValueAsString(product), headers);
        ResponseEntity<String> responseEntity = restTemplate.exchange(SHOP_ENDPOINT + "/products/", HttpMethod.POST, requestEntity, String.class);
        String data = responseEntity.getBody().toString();
        return objectMapper.readValue(data, Product.class);
    }

    private ShoppingCart createShoppingCart(ObjectMapper objectMapper, WebUser webUser) throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> requestEntity = new HttpEntity<String>("{}", headers);

        ResponseEntity<String> responseEntity = restTemplate.exchange(SHOP_ENDPOINT + "/cart/user/" + webUser.getUuid(), HttpMethod.POST, requestEntity, String.class);
        String data = responseEntity.getBody().toString();
        ShoppingCart cart = objectMapper.readValue(data, ShoppingCart.class);

        LOG.info("Created cart:" + cart.getUuid() + " for Webuser: " + webUser.getUsername());

        return cart;
    }

    private Account createAcccount(ObjectMapper objectMapper, WebUser webUser) throws Exception {
        Account account = new Account(UUID.randomUUID(), "address1", "phoneNumber1", "email1");

        LOG.info("Created account:" + account.getUuid() + " for Webuser: " + webUser.getUsername());

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> requestEntity = new HttpEntity<String>(objectMapper.writeValueAsString(account), headers);
        ResponseEntity<String> responseEntity = restTemplate.exchange(SHOP_ENDPOINT + "/accounts/user/" + webUser.getUuid(), HttpMethod.POST, requestEntity, String.class);

        String data = responseEntity.getBody().toString();
        return objectMapper.readValue(data, Account.class);
    }

    private WebUser createWebUser(ObjectMapper objectMapper) throws Exception {
        WebUserResource webUserResource = new WebUserResource("webuser" + UUID.randomUUID(), "password");

        LOG.info("Created user:" + webUserResource.getUsername());

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> requestEntity = new HttpEntity<String>(objectMapper.writeValueAsString(webUserResource), headers);
        ResponseEntity<String> responseEntity = restTemplate.exchange(SHOP_ENDPOINT + "/users/register", HttpMethod.POST, requestEntity, String.class);
        String data = responseEntity.getBody().toString();
        return objectMapper.readValue(data, WebUser.class);
    }

    private void waitForNewOrderToBeProcessed() throws InterruptedException {
        Thread.sleep(1000);
    }
}
