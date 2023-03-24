package io.synthesized.microservices.dto;

import java.util.List;

public record FilmData(Integer filmId, String title, List<PaymentData> payments) {
}
