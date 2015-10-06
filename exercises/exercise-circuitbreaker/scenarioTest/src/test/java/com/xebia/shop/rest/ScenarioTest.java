package com.xebia.shop.rest;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.apache.http.HttpStatus;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xebia.shop.domain.Shipment;
import com.xebia.shop.Application;
import com.xebia.shop.domain.Account;
import com.xebia.shop.domain.Orderr;
import com.xebia.shop.domain.Product;
import com.xebia.shop.domain.ShoppingCart;
import com.xebia.shop.domain.WebUser;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
public class ScenarioTest  {
    public static final String PAY_ENDPOINT = "http://localhost:9001/payment";
    public static final String SHOP_ENDPOINT = "http://localhost:9002/cart";
    public static final String FF_ENDPOINT = "http://localhost:9003/fulfillment";
    private static Logger LOG = LoggerFactory.getLogger(ScenarioTest.class);

    Random rnd = new Random();
    
    @Test
    public void createAccountAndOrderStuff() throws Exception {

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        // as there is no cleanup of the shopping cart and order yet, we need to create a new user, account and cart for each request
       Product[] products = new Product[10];
       for(int i=0; i<10; i++){
    	   products[i] = createProduct(objectMapper);
       }
        
       int i = 0;
        while (i < 5) {

           WebUser webUser = createWebUser(objectMapper);
           WebUser remoteUser = getWebUser(objectMapper, webUser);
           //LOG.info("Read back user: " + remoteUser.toString());

           Account account = createAcccount(objectMapper, webUser);           
           ShoppingCart cart = createShoppingCart(objectMapper, webUser);
           addProductToCart(objectMapper, cart, products[i]);
           Orderr orderr = createOrderForWebUser(objectMapper, webUser);
           addAccountToOrder(objectMapper, account, orderr);

           PaymentResponse paymentResponse = initiateOrderPayment(objectMapper, orderr);
           if (paymentResponse.getDescription().equals("CARD")) {
               PaymentResource paymentResource = payPayment(objectMapper, paymentResponse);
           }
           else{
               registerNonCardPayment(objectMapper, orderr);
           }
           approveOrder(orderr);
                      
           shipOrder(orderr.getUuid());

           i++;
       }
    }

    private void shipOrder(UUID orderID) throws Exception {
    	RestTemplate restTemplate = new RestTemplate();       
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> requestEntity = new HttpEntity<String>("{}", headers);
        ResponseEntity<String> responseEntity = restTemplate.exchange(FF_ENDPOINT + "/shipIt/"+orderID, HttpMethod.PUT, requestEntity, String.class);
 	    LOG.info("Orderr: " + orderID + " was shipped");
    }
    
    private PaymentResource payPayment(ObjectMapper objectMapper, PaymentResponse paymentResponse) throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("registerurl","http://localhost:9002/cart/orders/registerPayment/{id}");
        HttpEntity<String> requestEntity = new HttpEntity<String>("", headers);
        Map<String, String> params = new HashMap<String, String>();
        params.put("payid", paymentResponse.getUuid().toString());
        ResponseEntity<String> responseEntity = restTemplate.exchange(PAY_ENDPOINT + "/pay/{payid}/creditcard/1234", HttpMethod.PUT, requestEntity, String.class, params);
        String data = responseEntity.getBody().toString();
        PaymentResource paymentResource = objectMapper.readValue(data, PaymentResource.class);
        LOG.info("Payment: " + paymentResource.getUuid() + " has been paid.");
        return paymentResource;
    }

    private PaymentResponse initiateOrderPayment(ObjectMapper objectMapper, Orderr orderr) throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        Map<String, String> params = new HashMap<String, String>();
        params.put("orderrId", orderr.getUuid().toString());
        HttpEntity<String> requestEntity = new HttpEntity<String>("", headers);
        ResponseEntity<String> responseEntity = restTemplate.exchange(SHOP_ENDPOINT + "/orders/{orderrId}/pay", HttpMethod.PUT, requestEntity, String.class, params);
        String data = responseEntity.getBody().toString();
        PaymentResponse paymentResponse = objectMapper.readValue(data, PaymentResponse.class);
        LOG.info("Initiated payment for orderr: " + orderr.getUuid());
        return paymentResponse;
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
        ResponseEntity<String> responseEntity = restTemplate.exchange(SHOP_ENDPOINT + "/user/" + webUser.getUuid(), HttpMethod.GET, requestEntity, String.class);
        String data = responseEntity.getBody().toString();
        return objectMapper.readValue(data, WebUser.class);
    }

    private ShoppingCart addProductToCart(ObjectMapper objectMapper, ShoppingCart cart, Product product) throws Exception {        
        LOG.info("Adding product: " + product.getUuid() + " to cart: " + cart.getUuid());
        
        //LineItem lineItem = new LineItem(rnd.nextInt(10)+1, product.getPrice(), product);
        NewLineItemResource lineItem = new NewLineItemResource(product.getUuid(), rnd.nextInt(10)+1);
    	
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
    	Product product = new Product(UUID.randomUUID(), "product"+rnd.nextInt(1000), rnd.nextDouble()*100);
        
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
        
        ResponseEntity<String> responseEntity = restTemplate.exchange(SHOP_ENDPOINT + "/user/" + webUser.getUuid() + "/cart", HttpMethod.POST, requestEntity, String.class);
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
        ResponseEntity<String> responseEntity = restTemplate.exchange(SHOP_ENDPOINT + "/account/user/" + webUser.getUuid(), HttpMethod.POST, requestEntity, String.class);

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
        ResponseEntity<String> responseEntity = restTemplate.exchange(SHOP_ENDPOINT + "/user/register", HttpMethod.POST, requestEntity, String.class);
        String data = responseEntity.getBody().toString();
        return objectMapper.readValue(data, WebUser.class);
    }

    private void registerNonCardPayment(ObjectMapper objectMapper, Orderr orderr){

        LOG.info("registerNonCardPayment for order "+ orderr.getUuid().toString());

        //register payment with shop service
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        Map<String, String> params = new HashMap<String, String>();
        params.put("id", orderr.getUuid().toString());
        restTemplate.put("http://localhost:9002/cart/orders/registerPayment/{id}", String.class, params);



    }

}