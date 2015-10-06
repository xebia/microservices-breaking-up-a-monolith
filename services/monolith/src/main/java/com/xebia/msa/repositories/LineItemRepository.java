package com.xebia.msa.repositories;

import com.xebia.msa.domain.LineItem;
import org.springframework.data.repository.CrudRepository;

public interface LineItemRepository extends CrudRepository<LineItem, String> {
}
