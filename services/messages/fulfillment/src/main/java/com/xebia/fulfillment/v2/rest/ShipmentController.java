package com.xebia.fulfillment.v2.rest;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xebia.fulfillment.v2.Config;
import com.xebia.fulfillment.v2.domain.Clerk;
import com.xebia.fulfillment.v2.domain.Shipment;
import com.xebia.fulfillment.v2.repositories.ClerkRepository;
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
public class ShipmentController {
    private ObjectMapper mapper = new ObjectMapper();

    private static Logger LOG = LoggerFactory.getLogger(ShipmentController.class);

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Autowired
    private ShipmentRepository shipmentRepository;

    @Autowired
    ClerkRepository clerkRepository;

    private ShipmentResourceAssembler shipmentResourceAssembler = new ShipmentResourceAssembler();

    public ShipmentController() {
        mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{id}")
    public ResponseEntity<ShipmentResource> viewShipment(@PathVariable UUID uuid, HttpServletRequest request) {
        LOG.info("URL: " + request.getRequestURL() + ", METHOD: " + request.getMethod());
        Shipment shipment = shipmentRepository.findOne(uuid);
        if (shipment == null) {
            return new ResponseEntity<ShipmentResource>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<ShipmentResource>(shipmentResourceAssembler.toResource(shipment), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/shipIt/{shipmentId}")
    public ResponseEntity<ShipmentResource> shipIt(@PathVariable UUID shipmentId, HttpServletRequest request) {
        LOG.info("URL: " + request.getRequestURL() + ", METHOD: " + request.getMethod() + ", CONTENT: shipmentId=" + shipmentId.toString());
        try {
        Shipment shipment = updateDocument(shipmentId);
            return new ResponseEntity(shipmentResourceAssembler.toResource(shipment), HttpStatus.OK);
        } catch (Exception e) {
                LOG.info("shipment: " + shipmentId + " not found");
                return new ResponseEntity<ShipmentResource>(HttpStatus.NOT_FOUND);
            }
    }

    protected Shipment updateDocument(UUID shipmentId) throws Exception {
        Shipment shipment = shipmentRepository.findOne(shipmentId);
        if (shipment == null) {
            LOG.info("shipment: " + shipmentId + " not found");
            throw new NoDataFoundException();
        }
        shipment.ship();
        shipment = shipmentRepository.save(shipment);
        Clerk clerk = clerkRepository.findByShipment(shipment);

        LOG.info("Sending orderShipped event, new document: \n" + clerk.getDocument() + "\n");
        rabbitTemplate.convertAndSend(Config.shopExchange, Config.orderShipped, clerk.getDocument());
        return shipment;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/forClerk/{clerkId}", produces = "application/json")
    public ResponseEntity<ShipmentResource> findShipmentByClerk(@PathVariable UUID clerkId, HttpServletRequest request) {
        LOG.info("URL: " + request.getRequestURL() + ", METHOD: " + request.getMethod() + ", CONTENT: clerkId=" + clerkId.toString());
        Clerk clerk = clerkRepository.findOne(clerkId);
        Shipment shipment = clerk.getShipment();
        ShipmentResource resource = new ShipmentResourceAssembler().toResource(shipment);
        return new ResponseEntity(resource, HttpStatus.OK);
    }

}
