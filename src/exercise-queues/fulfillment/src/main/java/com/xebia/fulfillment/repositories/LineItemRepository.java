package com.xebia.fulfillment.repositories;

import com.xebia.fulfillment.domain.LineItem;
import org.springframework.data.repository.CrudRepository;

public interface LineItemRepository extends CrudRepository<LineItem, String> {
}
