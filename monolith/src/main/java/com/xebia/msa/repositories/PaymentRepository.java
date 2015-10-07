package com.xebia.msa.repositories;

import com.xebia.msa.domain.Payment;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface PaymentRepository extends CrudRepository<Payment, UUID> {

}

