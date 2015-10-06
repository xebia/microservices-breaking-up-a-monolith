package com.xebia.shop.repositories;

import org.springframework.data.repository.CrudRepository;

import com.xebia.shop.domain.LineItem;

public interface LineItemRepository extends CrudRepository<LineItem, String> {
}
