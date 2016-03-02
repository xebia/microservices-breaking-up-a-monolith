package com.xebia.fulfillment.v2.repositories;

import com.xebia.fulfillment.v2.domain.Document;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface DocumentRepository extends CrudRepository<Document, UUID> {
    Document findByClerkUuid(UUID clerkUuid);
}
