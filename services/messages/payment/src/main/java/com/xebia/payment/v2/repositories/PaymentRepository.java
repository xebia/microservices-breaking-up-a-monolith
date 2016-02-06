package com.xebia.payment.v2.repositories;

import com.xebia.payment.v2.domain.Clerk;
import com.xebia.payment.v2.domain.Payment;
import org.springframework.data.repository.CrudRepository;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public interface PaymentRepository extends CrudRepository<Payment, UUID> {
    List<Payment> findByDatePaidGreaterThan(Date date);
}
