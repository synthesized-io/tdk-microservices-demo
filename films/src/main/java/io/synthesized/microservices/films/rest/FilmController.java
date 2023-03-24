package io.synthesized.microservices.films.rest;

import io.synthesized.microservices.dto.FilmData;
import io.synthesized.microservices.films.service.FilmService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/v1/films")
public class FilmController {
    private final FilmService filmService;

    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping("/{filmId}")
    public FilmData getFilmInfo(@PathVariable Integer filmId) throws ExecutionException, InterruptedException {
        return filmService.getFilmInfo(filmId);
    }
}
