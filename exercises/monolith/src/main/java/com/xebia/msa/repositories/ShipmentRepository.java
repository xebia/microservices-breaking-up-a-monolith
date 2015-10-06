package com.xebia.msa.repositories;

import com.xebia.msa.domain.Orderr;
import com.xebia.msa.domain.Shipment;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface ShipmentRepository extends CrudRepository<Shipment, UUID> {
//    Shipment findByOrderUUID(UUID orderUUID);
    Shipment findByOrderr(Orderr orderr);
}

