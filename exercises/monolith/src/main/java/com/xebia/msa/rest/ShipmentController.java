package com.xebia.msa.rest;

import com.xebia.msa.domain.InvalidStatusException;
import com.xebia.msa.domain.Orderr;
import com.xebia.msa.domain.Shipment;
import com.xebia.msa.repositories.OrderRepository;
import com.xebia.msa.repositories.ShipmentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/monolith/shipment")
public class ShipmentController {

    private static Logger LOG = LoggerFactory.getLogger(ShipmentController.class);

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ShipmentRepository shipmentRepository;

    private ShipmentResourceAssembler shipmentResourceAssembler = new ShipmentResourceAssembler();

    @RequestMapping(method = RequestMethod.GET, value = "/{id}")
    public ResponseEntity<ShipmentResource> viewShipment(@PathVariable UUID id) {
        Shipment shipment = shipmentRepository.findOne(id);
        if (shipment == null) {
            return new ResponseEntity<ShipmentResource>(HttpStatus.NOT_FOUND);
        }
        ShipmentResource resource = shipmentResourceAssembler.toResource(shipment);
        return new ResponseEntity<ShipmentResource>(resource, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/getByOrder/{id}")
    public ResponseEntity<ShipmentResource> getByOrder(@PathVariable UUID id) {
    	Orderr orderr = orderRepository.findOne(id);
        Shipment shipment = shipmentRepository.findByOrderr(orderr);
        if (shipment == null) {
        	LOG.info("No shipment found for Order ID: " + id);
            return new ResponseEntity<ShipmentResource>(HttpStatus.NOT_FOUND);
        }
        ShipmentResource resource = shipmentResourceAssembler.toResource(shipment);
        return new ResponseEntity<ShipmentResource>(resource, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/shipIt/{id}")
    public ResponseEntity<ShipmentResource> shipIt(@PathVariable UUID id) {
    	
    	LOG.info("Shipment ID from path: " + id);
    	
        Shipment shipment = shipmentRepository.findOne(id);
        if (shipment == null) {
            return new ResponseEntity<ShipmentResource>(HttpStatus.NOT_FOUND);
        }
        try {
            shipment.ship();
            shipment = shipmentRepository.save(shipment);
        } catch (InvalidStatusException e) {
            return new ResponseEntity<ShipmentResource>(HttpStatus.NOT_ACCEPTABLE);
        }
        ShipmentResource resource = shipmentResourceAssembler.toResource(shipment);
        return new ResponseEntity<ShipmentResource>(resource, HttpStatus.OK);
    }

}
