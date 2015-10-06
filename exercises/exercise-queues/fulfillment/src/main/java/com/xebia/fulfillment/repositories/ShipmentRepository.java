package com.xebia.fulfillment.repositories;

import com.xebia.fulfillment.domain.Orderr;
import com.xebia.fulfillment.domain.Shipment;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface ShipmentRepository extends CrudRepository<Shipment, UUID> {
    Shipment findByOrderr(Orderr orderr);
}

