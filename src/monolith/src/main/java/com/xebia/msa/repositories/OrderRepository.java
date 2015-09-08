package com.xebia.msa.repositories;

import com.xebia.msa.domain.Orderr;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface OrderRepository extends CrudRepository<Orderr, UUID> {
}

