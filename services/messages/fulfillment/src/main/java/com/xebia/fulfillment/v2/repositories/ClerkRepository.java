package com.xebia.fulfillment.v2.repositories;

import com.xebia.fulfillment.v2.domain.Clerk;
import com.xebia.fulfillment.v2.domain.Shipment;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface ClerkRepository extends CrudRepository<Clerk, UUID> {
    Clerk findByShipment(Shipment shipment);
}
