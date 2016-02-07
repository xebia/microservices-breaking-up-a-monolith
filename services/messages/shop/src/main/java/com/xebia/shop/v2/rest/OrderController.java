package com.xebia.shop.v2.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xebia.shop.v2.Config;
import com.xebia.shop.v2.domain.Clerk;
import com.xebia.shop.v2.domain.Orderr;
import com.xebia.shop.v2.repositories.ClerkRepository;
import com.xebia.shop.v2.repositories.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/shop/v2/orders/")
public class OrderController {
	
    private static Logger LOG = LoggerFactory.getLogger(OrderController.class);
    private ObjectMapper mapper = new ObjectMapper();

	@Autowired
	RabbitTemplate rabbitTemplate;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ClerkRepository clerkRepository;

    private OrderResourceAssembler orderAssembler = new OrderResourceAssembler();

    @RequestMapping(method = RequestMethod.GET, value = "/{orderId}")
    public ResponseEntity<OrderResource> viewOrder(@PathVariable UUID orderId, HttpServletRequest request) {
        LOG.info("URL: "+ request.getRequestURL()+ ", METHOD: "+ request.getMethod());
        Orderr orderr = orderRepository.findOne(orderId);

        if (orderr == null) {
            return new ResponseEntity<OrderResource>(HttpStatus.NOT_FOUND);
        }
        OrderResource resource = orderAssembler.toResource(orderr);

        return new ResponseEntity<OrderResource>(resource, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.PUT, consumes = "application/json", produces = "application/json")
    public ResponseEntity<OrderResource> updateOrder(@RequestBody Orderr orderr, HttpServletRequest request) {
        LOG.info("URL: " + request.getRequestURL() + ", METHOD: " + request.getMethod() + ", CONTENT: orderId=" + orderr.getUuid());
        orderr = orderRepository.save(orderr);
        OrderResource resource = orderAssembler.toResource(orderr);
        return new ResponseEntity<OrderResource>(resource, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/{orderId}/approve", produces = "application/json")
    public ResponseEntity<OrderResource> approveOrder(@PathVariable UUID orderId, HttpServletRequest request) {
        LOG.info("URL: "+ request.getRequestURL()+ ", METHOD: "+ request.getMethod()+ ", CONTENT: orderId="+orderId.toString());
        Orderr orderr = orderRepository.findOne(orderId);

        if (orderr.canBeApproved()) {
            orderr.setStatus("approved");
            orderr = orderRepository.save(orderr);
            LOG.info("Order " + orderId + " approved.");

            try {
                Clerk clerk = clerkRepository.findByOrderr(orderr);
                LOG.info("Sending orderCompleted " + mapper.writeValueAsString(clerk));
                rabbitTemplate.convertAndSend(Config.shopExchange, Config.orderCompleted, mapper.writeValueAsString(clerk));
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

    @RequestMapping(method = RequestMethod.GET)
    public List<OrderResource> allOrders(HttpServletRequest request) {
        LOG.info("URL: "+ request.getRequestURL()+ ", METHOD: "+ request.getMethod());
        List<OrderResource> orderResources = new ArrayList<OrderResource>();
        orderResources = orderAssembler.toResources(orderRepository.findAll());
        return orderResources;
    }
}
