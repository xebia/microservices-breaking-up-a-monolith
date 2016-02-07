package com.xebia.shop.v2.repositories;

import com.xebia.shop.v2.domain.Orderr;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface OrderRepository extends CrudRepository<Orderr, UUID> {
}

