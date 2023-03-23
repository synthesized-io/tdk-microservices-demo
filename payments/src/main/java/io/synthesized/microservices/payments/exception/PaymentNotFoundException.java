package io.synthesized.microservices.payments.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.ErrorResponseException;

public class PaymentNotFoundException extends ErrorResponseException {
    public PaymentNotFoundException() {
        super(HttpStatus.NOT_FOUND);
    }
}