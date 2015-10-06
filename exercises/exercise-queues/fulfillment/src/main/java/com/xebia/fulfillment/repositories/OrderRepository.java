package com.xebia.fulfillment.repositories;

import com.xebia.fulfillment.domain.Orderr;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface OrderRepository extends CrudRepository<Orderr, UUID> {
}

