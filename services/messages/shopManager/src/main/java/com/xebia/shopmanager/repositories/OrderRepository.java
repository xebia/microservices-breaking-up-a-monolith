package com.xebia.shopmanager.repositories;

import com.xebia.shopmanager.domain.Orderr;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface OrderRepository extends CrudRepository<Orderr, UUID> {
}

