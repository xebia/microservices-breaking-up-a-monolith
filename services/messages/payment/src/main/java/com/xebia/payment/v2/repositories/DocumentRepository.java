package com.xebia.payment.v2.repositories;

import com.xebia.payment.v2.domain.Document;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface DocumentRepository extends CrudRepository<Document, UUID> {
    Document findByClerkUuid(UUID clerkUuid);
}
