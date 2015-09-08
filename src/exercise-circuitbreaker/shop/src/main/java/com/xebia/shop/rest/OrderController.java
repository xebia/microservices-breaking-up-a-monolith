package com.xebia.shop.rest;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixCommandMetrics;
import com.netflix.hystrix.HystrixRequestLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.hystrix.strategy.concurrency.HystrixRequestContext;
import com.xebia.shop.domain.Account;
import com.xebia.shop.domain.Orderr;
import com.xebia.shop.domain.WebUser;
import com.xebia.shop.repositories.AccountRepository;
import com.xebia.shop.repositories.OrderRepository;
import com.xebia.shop.repositories.ShoppingCartRepository;
import com.xebia.shop.repositories.WebUserRepository;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/cart/orders/")

public class OrderController {
	
    private static Logger LOG = LoggerFactory.getLogger(OrderController.class);
    private ObjectMapper mapper = new ObjectMapper();

    final static String shopExchange = "shop";
    final static String orderRoutingKey = "orders";

    @Autowired
    private WebUserRepository webUserRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ShoppingCartRepository shoppingCartRepository;

    private OrderResourceAssembler orderAssembler = new OrderResourceAssembler();

    @Autowired
    private AccountRepository accountRepository;

    @RequestMapping(method = RequestMethod.GET)
    public List<OrderResource> allOrders(HttpServletRequest request) {
        LOG.info("URL: "+ request.getRequestURL()+ ", METHOD: "+ request.getMethod());
        List<OrderResource> orderResources = new ArrayList<OrderResource>();
        orderResources = orderAssembler.toResources(orderRepository.findAll());
        return orderResources;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{id}")
    public ResponseEntity<OrderResource> viewOrder(@PathVariable UUID id, HttpServletRequest request) {
        LOG.info("URL: "+ request.getRequestURL()+ ", METHOD: "+ request.getMethod());
        Orderr orderr = orderRepository.findOne(id);

        if (orderr == null) {
            return new ResponseEntity<OrderResource>(HttpStatus.NOT_FOUND);
        }
        OrderResource resource = orderAssembler.toResource(orderr);

        return new ResponseEntity<OrderResource>(resource, HttpStatus.OK);
    }


    @RequestMapping(method = RequestMethod.PUT, value = "/{orderId}/account", consumes = "application/json", produces = "application/json")
    public ResponseEntity<OrderResource> orderShoppingCart(@PathVariable UUID orderId, @RequestBody AccountResource accountResource, HttpServletRequest request) {
        LOG.info("URL: "+ request.getRequestURL()+ ", METHOD: "+ request.getMethod()+ ", CONTENT: orderId="+orderId.toString()+", "+accountResource.toString());
        Orderr orderr = orderRepository.findOne(orderId);
        Account account = new Account(accountResource.getAddress(), accountResource.getPhoneNumber(), accountResource.getEmail());
        accountRepository.save(account);
        orderr.setAccount(account);
        orderr = orderRepository.save(orderr);
        OrderResource resource = orderAssembler.toResource(orderr);
        return new ResponseEntity<OrderResource>(resource, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/{orderId}/approve", consumes = "application/json", produces = "application/json")
    public ResponseEntity<OrderResource> approveOrder(@PathVariable UUID orderId, HttpServletRequest request) {
        LOG.info("URL: "+ request.getRequestURL()+ ", METHOD: "+ request.getMethod()+ ", CONTENT: orderId="+orderId.toString());
        Orderr orderr = orderRepository.findOne(orderId);
        
        if (orderr.canBeApproved()) {
            orderr.setStatus("approved");
            orderr = orderRepository.save(orderr);
            LOG.info(">>>> Order ready to be shipped. <<<<<");

            // call Shipment service via REST call that shipment can be shipped
            ObjectMapper objectMapper = new ObjectMapper();
            String FF_ENDPOINT = "http://localhost:9003/fulfillment";

            try {
				RestTemplate restTemplate = new RestTemplate();
				HttpHeaders headers = new HttpHeaders();
				headers.setContentType(MediaType.APPLICATION_JSON);
				HttpEntity<String> requestEntity = new HttpEntity<String>(objectMapper.writeValueAsString(orderr), headers);
				restTemplate.exchange(FF_ENDPOINT + "/orders", HttpMethod.POST, requestEntity, String.class);
			} catch (RestClientException e) {
				e.printStackTrace();
                throw e;
			} catch (JsonProcessingException e) {
				e.printStackTrace();

			}

            OrderResource resource = orderAssembler.toResource(orderr);
            return new ResponseEntity<OrderResource>(resource, HttpStatus.OK);
        } else {
            OrderResource resource = orderAssembler.toResource(orderr);
            LOG.info("Could not approve order with ID: " + orderId);
            return new ResponseEntity<OrderResource>(resource, HttpStatus.FORBIDDEN);
        }
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/{orderId}/pay", consumes = "application/json")
    public ResponseEntity<PaymentResponse> pay(@PathVariable UUID orderId, HttpServletRequest request) {
        LOG.info("URL: "+ request.getRequestURL()+ ", METHOD: "+ request.getMethod()+ ", CONTENT: orderId="+orderId.toString());
        LOG.info("Order ID to pay: " + orderId);
        Orderr orderr = orderRepository.findOne(orderId);

        HystrixRequestContext context = HystrixRequestContext.initializeContext();
        PaymentResponse resp = null;
        try{

            // Exercise: this is the call to the Hystrix Command
            resp = new StartPaymentCommand(orderr, false).execute();

            LOG.info("Logged Requests: "+HystrixRequestLog.getCurrentRequest().getExecutedCommandsAsString());

        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            context.shutdown();
        }

        return new ResponseEntity<PaymentResponse>(resp, HttpStatus.OK);

    }

    @RequestMapping(method = RequestMethod.POST, value = "add", produces = "application/json", consumes = "application/json")
    public ResponseEntity<OrderResource> createNewOrder(@RequestBody UUID webUserUUID, HttpServletRequest request) {
        LOG.info("URL: "+ request.getRequestURL()+ ", METHOD: "+ request.getMethod()+ ", CONTENT: webUserId="+webUserUUID.toString());
        LOG.info("User ID from body: " + webUserUUID);

        WebUser webUser = webUserRepository.findByUuid(webUserUUID);
        Orderr orderr = new Orderr(UUID.randomUUID(), webUser.getShoppingCart().getCreated(), webUser.getAccount().getAddress(), "Ordered");
        orderr.setAccount(webUser.getAccount());
        orderr.setShoppingCart(webUser.getShoppingCart());
        orderr = orderRepository.save(orderr);
        OrderResource responseResource = new OrderResourceAssembler().toResource(orderr);
        return new ResponseEntity<OrderResource>(responseResource, HttpStatus.CREATED);
    }

    @RequestMapping(method = RequestMethod.PUT, value = "registerPayment/{orderId}", consumes = "application/json", produces = "application/json")
    public ResponseEntity<OrderResource> registerPayment(@PathVariable UUID orderId, HttpServletRequest request) {
        LOG.info("URL: "+ request.getRequestURL()+ ", METHOD: "+ request.getMethod()+ ", CONTENT: orderId="+orderId.toString());
        Orderr orderr = orderRepository.findOne(orderId);
        orderr.setPaymentReceived(true);
        orderr = orderRepository.save(orderr);
        OrderResource resource = orderAssembler.toResource(orderr);
        return new ResponseEntity<OrderResource>(resource, HttpStatus.OK);
    }

}
