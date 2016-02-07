package com.xebia.fulfillment.v2.repositories;

import com.xebia.fulfillment.v2.domain.Product;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface ProductRepository extends CrudRepository<Product, UUID> {

}

