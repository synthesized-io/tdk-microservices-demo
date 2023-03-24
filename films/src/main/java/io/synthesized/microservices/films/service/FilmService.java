package io.synthesized.microservices.films.service;

import io.synthesized.microservices.dto.FilmData;
import io.synthesized.microservices.dto.PaymentData;
import io.synthesized.microservices.films.exception.FilmNotFoundException;
import io.synthesized.microservices.films.repository.FilmRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class FilmService {
    private final RestTemplate restTemplate;
    private final FilmRepository filmRepository;
    private final AsyncTaskExecutor taskExecutor;
    private final String paymentsServiceUrl;

    public FilmService(FilmRepository filmRepository, RestTemplate restTemplate, AsyncTaskExecutor taskExecutor,
                       @Value("${payments.service.url}") String paymentServiceUrl) {
        this.filmRepository = filmRepository;
        this.restTemplate = restTemplate;
        this.taskExecutor = taskExecutor;
        this.paymentsServiceUrl = paymentServiceUrl;
    }

    public FilmData getFilmInfo(Integer filmId) throws ExecutionException, InterruptedException {
        var baseFilmData = taskExecutor.submitCompletable(() ->
                filmRepository
                        .findById(filmId)
                        .orElseThrow(FilmNotFoundException::new)
        );

        var responseType = new ParameterizedTypeReference<List<PaymentData>>() {};

        var filmPayments = taskExecutor.submitCompletable(() ->
                restTemplate.exchange(
                        UriComponentsBuilder.fromHttpUrl(paymentsServiceUrl)
                                .queryParam("filmId", filmId).toUriString(),
                        HttpMethod.GET, null, responseType
                ).getBody()
        );

        return baseFilmData.thenCombine(filmPayments, (film, payments) ->
                new FilmData(film.getFilmId(), film.getTitle(), payments)
        ).get();
    }
}
