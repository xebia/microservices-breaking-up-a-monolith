package com.xebia.fulfillment.rest;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xebia.fulfillment.domain.InvalidStatusException;
import com.xebia.fulfillment.domain.Orderr;
import com.xebia.fulfillment.domain.Shipment;
import com.xebia.fulfillment.repositories.OrderRepository;
import com.xebia.fulfillment.repositories.ShipmentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.UUID;

// Exercise 4
// Use JsonIgnoreProperties to ignore properties that don't matter for us. This allows you to simplify the domain
// classes in the domain package.
@RestController
@RequestMapping("/fulfillment")
@JsonIgnoreProperties(ignoreUnknown=true)
public class ShipmentController {

    private static Logger LOG = LoggerFactory.getLogger(ShipmentController.class);

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ShipmentRepository shipmentRepository;

    private ShipmentResourceAssembler shipmentResourceAssembler = new ShipmentResourceAssembler();

    ObjectMapper objectMapper;

    public ShipmentController(){

        objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }
    // TODO: unpaid orders
    // TODO: retrieve order by uuid


    @RequestMapping(method = RequestMethod.GET, value = "/{id}")
    public ResponseEntity<ShipmentResource> viewShipment(@PathVariable UUID id, HttpServletRequest request) {
        LOG.info("URL: "+ request.getRequestURL()+ ", METHOD: "+ request.getMethod());
        Shipment shipment = shipmentRepository.findOne(id);
        if (shipment == null) {
            return new ResponseEntity<ShipmentResource>(HttpStatus.NOT_FOUND);
        }
        ShipmentResource resource = shipmentResourceAssembler.toResource(shipment);
        return new ResponseEntity<ShipmentResource>(resource, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/getByOrder/{id}")
    public ResponseEntity<ShipmentResource> getByOrder(@PathVariable UUID id, HttpServletRequest request) {
        LOG.info("URL: "+ request.getRequestURL()+ ", METHOD: "+ request.getMethod());
        Orderr orderr = orderRepository.findOne(id);
        Shipment shipment = shipmentRepository.findByOrderr(orderr);
        if (shipment == null) {
            return new ResponseEntity<ShipmentResource>(HttpStatus.NOT_FOUND);
        }
        ShipmentResource resource = shipmentResourceAssembler.toResource(shipment);
        return new ResponseEntity<ShipmentResource>(resource, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/shipIt/{orderId}")
    public ResponseEntity<ShipmentResource> shipIt(@PathVariable UUID orderId, HttpServletRequest request) {
        LOG.info("URL: "+ request.getRequestURL()+ ", METHOD: "+ request.getMethod()+ ", CONTENT: orderId="+orderId.toString());
        Orderr orderr = orderRepository.findOne(orderId);
        Shipment shipment = shipmentRepository.findByOrderr(orderr);
        if (shipment == null) {
            return new ResponseEntity<ShipmentResource>(HttpStatus.NOT_FOUND);
        }
        try {
            if (!orderr.isPaymentReceived()) {
                return new ResponseEntity<ShipmentResource>(HttpStatus.PAYMENT_REQUIRED);
            }
            if (shipment.getStatus().equals(Shipment.SHIPPABLE)) {
                shipment.ship();
                shipment = shipmentRepository.save(shipment);
            }
        } catch (InvalidStatusException e) {
            return new ResponseEntity<ShipmentResource>(HttpStatus.NOT_ACCEPTABLE);
        }
        ShipmentResource resource = shipmentResourceAssembler.toResource(shipment);
        return new ResponseEntity<ShipmentResource>(resource, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST, value= "/orders", produces = "application/json", consumes = "application/json")
    @JsonIgnoreProperties(ignoreUnknown = true)
    public ResponseEntity<ShipmentResource> createNewShipment(@RequestBody Orderr orderr, HttpServletRequest request) {
        LOG.info("URL: "+ request.getRequestURL()+ ", METHOD: "+ request.getMethod()+ ", CONTENT: "+ orderr.toString());
        orderr = orderRepository.save(orderr);
        Shipment shipment = new Shipment(UUID.randomUUID(), Shipment.TO_BE_PAID, orderr.getShippingAddress(), orderr);
        shipmentRepository.save(shipment);
        OrderResource responseResource = new OrderResourceAssembler().toResource(orderr);
        ShipmentResource shipmentResource = new ShipmentResourceAssembler().toResource(shipment);
        return new ResponseEntity<ShipmentResource>(shipmentResource, HttpStatus.CREATED);
    }

    // This method shouldn't be necessary. However, JsonIgnoreProperties doesn't work so instead of automagical Spring stuff
    // in @RequestBody we now use explicit parsing through objectMapper which does support skipping properties.
    @RequestMapping(method = RequestMethod.POST, value = "/orders/newOrder", consumes = "application/json", produces = "application/json")
    public ResponseEntity<ShipmentResource> orderFromString(@RequestBody String orderrAsString, HttpServletRequest request) {
        try {
            LOG.info("URL: "+ request.getRequestURL()+ ", METHOD: "+ request.getMethod()+ ", CONTENT: "+orderrAsString);
            Orderr orderr = objectMapper.readValue(orderrAsString, Orderr.class);
            orderr = orderRepository.save(orderr);
            Shipment shipment = new Shipment(UUID.randomUUID(), Shipment.TO_BE_PAID, orderr.getShippingAddress(), orderr);
            shipmentRepository.save(shipment);
            OrderResource responseResource = new OrderResourceAssembler().toResource(orderr);
            ShipmentResource shipmentResource = new ShipmentResourceAssembler().toResource(shipment);
            return new ResponseEntity<ShipmentResource>(shipmentResource, HttpStatus.CREATED);
        } catch (IOException e) {
            LOG.error("Error parsing order: " + orderrAsString);
            return new ResponseEntity("Error parsing order", HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(method = RequestMethod.PUT, value = "orders/{orderId}/paymentReceived", produces = "application/json")
    public ResponseEntity<OrderResource> paymentReceived(@PathVariable UUID orderId, HttpServletRequest request) {
        LOG.info("URL: "+ request.getRequestURL()+ ", METHOD: "+ request.getMethod()+ ", CONTENT: orderId="+orderId.toString());
        Orderr orderr = orderRepository.findOne(orderId);
        orderr.setPaymentReceived(true);
        orderr = orderRepository.save(orderr);
        OrderResource responseResource = new OrderResourceAssembler().toResource(orderr);
        Shipment shipment = shipmentRepository.findByOrderr(orderr);
        shipment.setStatus(Shipment.SHIPPABLE);
        shipmentRepository.save(shipment);

        return new ResponseEntity<OrderResource>(responseResource, HttpStatus.OK);
    }

}
