package io.synthesized.microservices.payments.repository;

import io.synthesized.microservices.payments.domain.Payment;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface PaymentRepository extends CrudRepository<Payment, Long> {
    List<Payment> findByRentalInventoryFilmId(Long filmId);
}
