package com.xebia.shop.rest;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixCommandMetrics;
import com.netflix.hystrix.HystrixRequestLog;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
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
	RabbitTemplate rabbitTemplate;

    @Autowired
    private WebUserRepository webUserRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ShoppingCartRepository shoppingCartRepository;

    private OrderResourceAssembler orderAssembler = new OrderResourceAssembler();

    @Autowired
    private AccountRepository accountRepository;

    @ApiOperation(value = "Returns a list of all orders", notes = "Returns the list of orders"
            , response = OrderResource.class, responseContainer = "List")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
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
            LOG.info("Order " + orderId + " approved.");

            // Exercise
            // This is where the REST version used to send information about the Order to
            // fulfillment.
            // A new Order is created, inform other services about this fact.
            try {
                rabbitTemplate.convertAndSend(shopExchange, orderRoutingKey, mapper.writeValueAsString(orderr));
            } catch (Exception e) {
                LOG.error("Error: " + e.getMessage());
            }

            OrderResource resource = orderAssembler.toResource(orderr);
            return new ResponseEntity<OrderResource>(resource, HttpStatus.OK);
        } else {
            OrderResource resource = orderAssembler.toResource(orderr);
            LOG.info("Could not approve order with ID: " + orderId);
            return new ResponseEntity<OrderResource>(resource, HttpStatus.FORBIDDEN);
        }
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

    // Exercise
    // Will Order register payment? This method won't be called anymore because Payment will send out an orderPaid event
    // That will be handled in events.EventListener
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
