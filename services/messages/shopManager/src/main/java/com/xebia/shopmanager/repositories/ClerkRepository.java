package com.xebia.shopmanager.repositories;

import com.xebia.shopmanager.domain.Clerk;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface ClerkRepository extends CrudRepository<Clerk, UUID> {
}
