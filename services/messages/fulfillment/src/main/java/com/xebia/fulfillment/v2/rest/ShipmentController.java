package com.xebia.fulfillment.v2.rest;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xebia.fulfillment.v2.Config;
import com.xebia.fulfillment.v2.domain.Clerk;
import com.xebia.fulfillment.v2.domain.Shipment;
import com.xebia.fulfillment.v2.repositories.ClerkRepository;
import com.xebia.fulfillment.v2.repositories.OrderRepository;
import com.xebia.fulfillment.v2.repositories.ShipmentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.UUID;

@RestController
@RequestMapping("/fulfillment/v2/")
@JsonIgnoreProperties(ignoreUnknown = true)
public class ShipmentController {

    private static Logger LOG = LoggerFactory.getLogger(ShipmentController.class);

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ShipmentRepository shipmentRepository;

    @Autowired
    ClerkRepository clerkRepository;

    private ShipmentResourceAssembler shipmentResourceAssembler = new ShipmentResourceAssembler();

    ObjectMapper mapper;

    public ShipmentController() {
        mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{id}")
    public ResponseEntity<ShipmentResource> viewShipment(@PathVariable UUID id, HttpServletRequest request) {
        LOG.info("URL: " + request.getRequestURL() + ", METHOD: " + request.getMethod());
        Shipment shipment = shipmentRepository.findOne(id);
        if (shipment == null) {
            return new ResponseEntity<ShipmentResource>(HttpStatus.NOT_FOUND);
        }
        ShipmentResource resource = shipmentResourceAssembler.toResource(shipment);
        return new ResponseEntity<ShipmentResource>(resource, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/shipIt/{shipmentId}")
    public ResponseEntity<ShipmentResource> shipIt(@PathVariable UUID shipmentId, HttpServletRequest request) {
        LOG.info("URL: " + request.getRequestURL() + ", METHOD: " + request.getMethod() + ", CONTENT: shipmentId=" + shipmentId.toString());
        Shipment shipment = shipmentRepository.findOne(shipmentId);
        if (shipment == null) {
            return new ResponseEntity<ShipmentResource>(HttpStatus.NOT_FOUND);
        }
        shipment.ship();
        shipment = shipmentRepository.save(shipment);
        Clerk clerk = clerkRepository.findByShipment(shipment);
        try {
            String clerkAsJson = mapper.writeValueAsString(clerk);
            LOG.info("Sending orderShipped event for clerk: \n" + clerkAsJson + "\n");
            rabbitTemplate.convertAndSend(Config.shopExchange, Config.orderShipped, clerkAsJson);
        } catch (Exception e) {
            LOG.error("Error: " + e.getMessage());
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
        ShipmentResource resource = shipmentResourceAssembler.toResource(shipment);
        return new ResponseEntity<ShipmentResource>(resource, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/forClerk/{clerkId}", produces = "application/json")
    public ResponseEntity<ShipmentResource> findShipmentByClerk(@PathVariable UUID clerkId, HttpServletRequest request) {
        LOG.info("URL: " + request.getRequestURL() + ", METHOD: " + request.getMethod() + ", CONTENT: clerkId=" + clerkId.toString());
        Clerk clerk = clerkRepository.findOne(clerkId);
        Shipment shipment = clerk.getShipment();
        ShipmentResource resource = new ShipmentResourceAssembler().toResource(shipment);
        return new ResponseEntity<ShipmentResource>(resource, HttpStatus.OK);
    }

}
