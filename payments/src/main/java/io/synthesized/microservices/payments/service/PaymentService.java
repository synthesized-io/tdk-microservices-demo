package io.synthesized.microservices.payments.service;

import io.synthesized.microservices.dto.PaymentData;
import io.synthesized.microservices.payments.exception.PaymentNotFoundException;
import io.synthesized.microservices.payments.repository.PaymentRepository;
import org.springframework.stereotype.Service;

import java.util.stream.Stream;

@Service
public class PaymentService {
    private final PaymentRepository paymentRepository;

    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    public PaymentData getPaymentInfo(Long id) {
        final var payment = paymentRepository
                .findById(id)
                .orElseThrow(PaymentNotFoundException::new);
        return new PaymentData(payment.getPaymentId(), payment.getAmount());
    }

    public Stream<PaymentData> getFilmPayments(Long filmId) {
        return paymentRepository.findByRentalInventoryFilmId(filmId).stream().map(p -> {
            return new PaymentData(p.getPaymentId(), p.getAmount());
        });
    }
}
