package com.xebia.payment.v2.repositories;

import com.xebia.payment.v2.domain.Product;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface ProductRepository extends CrudRepository<Product, UUID> {

}

