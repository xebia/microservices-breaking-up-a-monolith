package com.xebia.shop.v2.repositories;

import com.xebia.shop.v2.domain.LineItem;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface LineItemRepository extends CrudRepository<LineItem, UUID> {
}
