package com.xebia.shop.repositories;

import org.springframework.data.repository.CrudRepository;

import com.xebia.shop.domain.Product;

import java.util.UUID;

public interface ProductRepository extends CrudRepository<Product, UUID> {

}

