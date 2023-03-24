package io.synthesized.microservices.payments.rest;

import io.synthesized.microservices.dto.PaymentData;
import io.synthesized.microservices.payments.service.PaymentService;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Stream;

@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {
    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @GetMapping("/{id}")
    public PaymentData getPaymentInfo(@PathVariable Long id) {
        return paymentService.getPaymentInfo(id);
    }

    @GetMapping("/")
    public Stream<PaymentData> getFilmPayments(@RequestParam("filmId") Long filmId) {
        return paymentService.getFilmPayments(filmId);
    }
}
