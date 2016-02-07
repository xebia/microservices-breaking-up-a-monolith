package com.xebia.fulfillment.v2.repositories;

import com.xebia.fulfillment.v2.domain.Payment;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface PaymentRepository extends CrudRepository<Payment, UUID> {
}
