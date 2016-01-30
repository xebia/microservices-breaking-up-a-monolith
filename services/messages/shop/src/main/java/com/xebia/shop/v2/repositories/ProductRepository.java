package com.xebia.shop.v2.repositories;

import com.xebia.shop.v2.domain.Product;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface ProductRepository extends CrudRepository<Product, UUID> {

}

