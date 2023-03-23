package io.synthesized.microservices.films.repository;

import io.synthesized.microservices.films.domain.Film;
import org.springframework.data.repository.CrudRepository;

public interface FilmRepository extends CrudRepository<Film, Integer> {
}
