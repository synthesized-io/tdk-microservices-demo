package io.synthesized.microservices.films.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.ErrorResponseException;

public class FilmNotFoundException extends ErrorResponseException {
    public FilmNotFoundException() {
        super(HttpStatus.NOT_FOUND);
    }
}
