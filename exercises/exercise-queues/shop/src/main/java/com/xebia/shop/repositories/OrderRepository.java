package com.xebia.shop.repositories;

import org.springframework.data.repository.CrudRepository;

import com.xebia.shop.domain.Orderr;

import java.util.UUID;

public interface OrderRepository extends CrudRepository<Orderr, UUID> {
}

