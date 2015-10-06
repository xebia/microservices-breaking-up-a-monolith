package com.xebia.payment.repositories;

import com.xebia.payment.domain.Payment;
import org.springframework.data.repository.CrudRepository;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public interface PaymentRepository extends CrudRepository<Payment, UUID> {
    List<Payment> findByDatePaidGreaterThan(Date date);
}
