package com.xebia.msa.rest;

import com.xebia.msa.domain.*;
import com.xebia.msa.repositories.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

@RestController
@RequestMapping("/monolith/orders/")

public class OrderController {

    private static Logger LOG = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    private WebUserRepository webUserRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ShoppingCartRepository shoppingCartRepository;

    private OrderResourceAssembler orderAssembler = new OrderResourceAssembler();

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private ShipmentRepository shipmentRepository;

    @RequestMapping(method = RequestMethod.GET)
    public List<OrderResource> allOrders() {
        List<OrderResource> orderResources = new ArrayList<OrderResource>();
        orderResources = orderAssembler.toResources(orderRepository.findAll());
        return orderResources;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{id}")
    public ResponseEntity<OrderResource> viewOrder(@PathVariable UUID id) {

        Orderr orderr = orderRepository.findOne(id);

        if (orderr == null) {
            return new ResponseEntity<OrderResource>(HttpStatus.NOT_FOUND);
        }
        OrderResource resource = orderAssembler.toResource(orderr);

        return new ResponseEntity<OrderResource>(resource, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/{orderId}/account", consumes = "application/json", produces = "application/json")
    public ResponseEntity<OrderResource> orderShoppingCart(@PathVariable UUID orderId, @RequestBody AccountResource accountResource) {
        Orderr orderr = orderRepository.findOne(orderId);
        Account account = new Account(accountResource.getAddress(), accountResource.getPhoneNumber(), accountResource.getEmail());
        accountRepository.save(account);
        orderr.setAccount(account);
        orderr = orderRepository.save(orderr);
        OrderResource resource = orderAssembler.toResource(orderr);
        return new ResponseEntity<OrderResource>(resource, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/{orderId}/approve", consumes = "application/json", produces = "application/json")
    public ResponseEntity<UUID> approveOrder(@PathVariable UUID orderId) {
        Orderr orderr = orderRepository.findOne(orderId);
        if (orderr.canBeApproved()) {
            orderr.setStatus("approved");
            orderr = orderRepository.save(orderr);
            LOG.info(">>>> Order ready to be shipped. <<<<<");
            Shipment shipment = new Shipment(UUID.randomUUID(), Shipment.SHIPPABLE, orderr.getShippingAddress(), orderr);
            UUID shipmentid = shipmentRepository.save(shipment).getUuid();
            return new ResponseEntity<UUID>(shipmentid, HttpStatus.OK);
        } else {
            OrderResource resource = orderAssembler.toResource(orderr);
        	LOG.info("Could not approve order with ID: " + orderId);
            return new ResponseEntity<UUID>(orderId, HttpStatus.FORBIDDEN);
        }
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/{orderId}/pay", consumes = "application/json", produces = "application/json")
    public ResponseEntity<OrderResource> pay(@PathVariable UUID orderId) {
    	
    	LOG.info("Order ID to pay from body: " + orderId);
    	
        Payment payment = paymentRepository.save(new Payment(UUID.randomUUID(), new Date(), 10.0, "Payment"));
        Orderr orderr = orderRepository.findOne(orderId);
        orderr.setPayment(payment);
        orderr = orderRepository.save(orderr);
        OrderResource resource = orderAssembler.toResource(orderr);
        return new ResponseEntity<OrderResource>(resource, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST, value = "add", produces = "application/json", consumes = "application/json")
    public ResponseEntity<OrderResource> createNewOrder(@RequestBody UUID webUserUUID) {
    	
    	LOG.info("User ID from body: " + webUserUUID);
    	
        WebUser webUser = webUserRepository.findByUuid(webUserUUID);
        Orderr orderr = new Orderr(UUID.randomUUID(), webUser.getShoppingCart().getCreated(), webUser.getAccount().getAddress(), "Ordered");
        orderr.setAccount(webUser.getAccount());
        orderr.setShoppingCart(webUser.getShoppingCart());
        orderr = orderRepository.save(orderr);
        OrderResource responseResource = new OrderResourceAssembler().toResource(orderr);
        return new ResponseEntity<OrderResource>(responseResource, HttpStatus.CREATED);
    }
}
