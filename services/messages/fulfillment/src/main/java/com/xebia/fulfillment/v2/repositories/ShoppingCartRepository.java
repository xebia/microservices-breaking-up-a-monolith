package com.xebia.fulfillment.v2.repositories;

import com.xebia.fulfillment.v2.domain.ShoppingCart;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface ShoppingCartRepository extends CrudRepository<ShoppingCart, UUID> {
}
