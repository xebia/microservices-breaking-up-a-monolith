package com.xebia.shop.v2.rest;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xebia.shop.v2.Application;
import com.xebia.shop.v2.domain.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.Random;
import java.util.UUID;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
public class ScenarioTest {
    public static final String PROTOCOL_AND_HOST = "http://localhost";
    public static final String PAY_ENDPOINT = PROTOCOL_AND_HOST + ":9001/payment/v2";
    public static final String SHOP_ENDPOINT = PROTOCOL_AND_HOST + ":9002/shop/v2";
    public static final String FF_ENDPOINT = PROTOCOL_AND_HOST + ":9003/fulfillment/v2";
    public static final String SHOPMANAGER_ENDPOINT = PROTOCOL_AND_HOST + ":9005/shop";
    private static Logger LOG = LoggerFactory.getLogger(ScenarioTest.class);

    Random rnd = new Random();

    @Test
    public void createAccountAndOrderStuff() throws Exception {

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        LOG.info("Starting test ...");
        LOG.info("Creating some products");
        Product[] products = new Product[10];
        for (int i = 0; i < 10; i++) {
            products[i] = createProduct(objectMapper);
        }

        LOG.info("Creating WebUser");
        WebUser webUser = createWebUser(objectMapper);
        WebUser remoteUser = getWebUser(objectMapper, webUser);

        LOG.info("Creating Clerk");
        Clerk clerk = createClerk(objectMapper, webUser);
        LOG.info("clerk: " + objectMapper.writeValueAsString(clerk));
        waitASecond();

        LOG.info("Creating ShoppingCart");
        ShoppingCart cart = getShoppingCart(objectMapper, clerk);
        addProductToCart(objectMapper, cart, products[1]);
        LOG.info("Creating Orderr from ShoppingCart");
        Orderr orderr = createOrderForWebUser(objectMapper, cart);
        orderr = setShippingAddress(objectMapper, orderr);
        approveOrder(orderr);
        waitASecond();

        Clerk clerk2 = findClerk(objectMapper, clerk);
        assertEquals(clerk.getUuid(), clerk2.getUuid());
        assertNotNull(clerk2.getOrderr());
        LOG.info("clerk: " + objectMapper.writeValueAsString(clerk2));

        LOG.info("Creating Payment");
        Payment payment = findPayment(objectMapper, clerk2);
        sendPayment(objectMapper, payment);
        waitASecond();

        Clerk clerk3 = findClerk(objectMapper, clerk2);
        assertEquals(clerk.getUuid(), clerk3.getUuid());
        assertEquals("c123", clerk3.getPayment().getCardId());
        LOG.info("clerk: " + objectMapper.writeValueAsString(clerk3));

        LOG.info("Creating Shipment");
        Shipment shipment = findShipment(objectMapper, clerk3);
        shipOrder(objectMapper, shipment);
        waitASecond();

        Clerk clerk4 = findClerk(objectMapper, clerk3);
        assertEquals(clerk.getUuid(), clerk4.getUuid());
        assertEquals(Shipment.SHIPPED, clerk4.getShipment().getStatus());
        LOG.info("Process completed, clerk: " + objectMapper.writeValueAsString(clerk4));
    }

    private Shipment findShipment (ObjectMapper objectMapper, Clerk clerk) throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> requestEntity = new HttpEntity<String>("{}", headers);
        ResponseEntity<String> responseEntity = restTemplate.exchange(FF_ENDPOINT + "/forClerk/" + clerk.getUuid(), HttpMethod.GET, requestEntity, String.class);
        String data = responseEntity.getBody().toString();
        Shipment shipment = objectMapper.readValue(data, Shipment.class);
        return shipment;
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

    private void shipOrder(ObjectMapper objectMapper, Shipment shipment) throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> requestEntity = new HttpEntity<String>("{}", headers);
        ResponseEntity<String> responseEntity = restTemplate.exchange(FF_ENDPOINT + "/shipIt/" + shipment.getUuid(), HttpMethod.PUT, requestEntity, String.class);
        String data = responseEntity.getBody().toString();
        ShipmentResource shipmentResource = objectMapper.readValue(data, ShipmentResource.class);
        LOG.info("Shipment: " + shipmentResource.getUuid() + " has been sent.");
    }

    private Payment findPayment (ObjectMapper objectMapper, Clerk clerk) throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> requestEntity = new HttpEntity<String>("{}", headers);
        ResponseEntity<String> responseEntity = restTemplate.exchange(PAY_ENDPOINT + "/forClerk/" + clerk.getUuid(), HttpMethod.GET, requestEntity, String.class);
        String data = responseEntity.getBody().toString();
        Payment payment = objectMapper.readValue(data, Payment.class);
        return payment;
    }

    private Payment sendPayment(ObjectMapper objectMapper, Payment payment) throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        payment.setCardId("c123");
        payment.setDatePaid(new Date());
        payment.setDescription("desc");
        HttpEntity<String> requestEntity = new HttpEntity<String>(objectMapper.writeValueAsString(payment), headers);
        ResponseEntity<String> responseEntity = restTemplate.exchange(PAY_ENDPOINT + "/pay/" + payment.getUuid() +"/creditcard/" + payment.getCardId(), HttpMethod.PUT, requestEntity, String.class);
        String data = responseEntity.getBody().toString();
        PaymentResource paymentResource = objectMapper.readValue(data, PaymentResource.class);
        LOG.info("Payment: " + paymentResource.getUuid() + " has been paid.");
        Payment payment2 = objectMapper.readValue(data, Payment.class);
        assertEquals(payment2.getCardId(), "c123");
        assertEquals(payment2.getUuid(), payment.getUuid());
        return payment2;
    }

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
        LOG.info("Orderr: " + orderr.getUuid() + " - shippingAddress added");
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
        LOG.info("Created Orderr: " + data);
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
        NewLineItemResource lineItem = new NewLineItemResource(product.getUuid(), rnd.nextInt(10) + 1);
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> requestEntity = new HttpEntity<String>(objectMapper.writeValueAsString(lineItem), headers);
        ResponseEntity<String> responseEntity = restTemplate.exchange(SHOP_ENDPOINT + "/cart/" + cart.getUuid() + "/add", HttpMethod.POST, requestEntity, String.class);
        String data = responseEntity.getBody().toString();
        cart = objectMapper.readValue(data, ShoppingCart.class);
        LOG.info("Product: " + product.getUuid() + " added to cart: " + cart.getUuid());
        return cart;
    }

    private Product createProduct(ObjectMapper objectMapper) throws Exception {
        Product product = new Product(UUID.randomUUID(), "product" + rnd.nextInt(1000), "supplier", rnd.nextDouble() * 100);
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> requestEntity = new HttpEntity<String>(objectMapper.writeValueAsString(product), headers);
        ResponseEntity<String> responseEntity = restTemplate.exchange(SHOP_ENDPOINT + "/products/", HttpMethod.POST, requestEntity, String.class);
        String data = responseEntity.getBody().toString();
        LOG.info("Created product:" + data);
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
        LOG.info("Found shopping cart: " + data);
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
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> requestEntity = new HttpEntity<String>(objectMapper.writeValueAsString(webUserResource), headers);
        ResponseEntity<String> responseEntity = restTemplate.exchange(SHOPMANAGER_ENDPOINT + "/users/register", HttpMethod.POST, requestEntity, String.class);
        String data = responseEntity.getBody().toString();
        LOG.info("Created WebUser: " + data);
        return objectMapper.readValue(data, WebUser.class);
    }

    private void waitASecond() throws InterruptedException {
        LOG.info("Waiting a second for processing to complete");
        Thread.sleep(1000);
    }
}

