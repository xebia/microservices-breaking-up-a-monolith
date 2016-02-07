package com.xebia.fulfillment.v2.repositories;

import com.xebia.fulfillment.v2.domain.LineItem;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface LineItemRepository extends CrudRepository<LineItem, UUID> {
}
