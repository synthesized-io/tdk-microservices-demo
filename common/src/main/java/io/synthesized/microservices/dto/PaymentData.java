package io.synthesized.microservices.dto;

import java.math.BigDecimal;

public record PaymentData(Long paymentId, BigDecimal amount) {
}
