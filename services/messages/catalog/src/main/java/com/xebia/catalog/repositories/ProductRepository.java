package com.xebia.catalog.repositories;

import com.xebia.catalog.domain.Product;
import org.springframework.data.repository.CrudRepository;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public interface ProductRepository extends CrudRepository<Product, UUID> {
    List<Product> findByDateAddedGreaterThan(Date date);
}
