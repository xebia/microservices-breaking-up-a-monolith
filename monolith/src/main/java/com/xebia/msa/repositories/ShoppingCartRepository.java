package com.xebia.msa.repositories;

import com.xebia.msa.domain.ShoppingCart;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface ShoppingCartRepository extends CrudRepository<ShoppingCart, UUID> {
}
