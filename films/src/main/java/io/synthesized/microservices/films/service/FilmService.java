package io.synthesized.microservices.films.service;

import io.synthesized.microservices.dto.FilmData;
import io.synthesized.microservices.films.exception.FilmNotFoundException;
import io.synthesized.microservices.films.repository.FilmRepository;
import org.springframework.stereotype.Service;

@Service
public class FilmService {
    private final FilmRepository filmRepository;

    public FilmService(FilmRepository filmRepository) {
        this.filmRepository = filmRepository;
    }

    public FilmData getFilmInfo(Integer filmId) {
        final var film = filmRepository
                .findById(filmId)
                .orElseThrow(FilmNotFoundException::new);
        return new FilmData(film.getFilmId(), film.getTitle());
    }
}
