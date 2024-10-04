package ru.yandex.practicum.filmorate.service;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.util.*;
import java.util.stream.Collectors;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@AllArgsConstructor
@Service
public class GenreService {
    GenreStorage genreStorage;

    public Collection<Genre> getGenres() {
        return genreStorage.getGenres().stream()
                .sorted(Comparator.comparing(Genre::getId))
                .collect(Collectors.toList());
    }

    public Genre getGenreById(Integer id) {
        return genreStorage.getGenreById(id);
    }

    public void putGenres(Film film) {
        List<Genre> distinct = film.getGenres().stream().distinct().toList();

        film.getGenres().clear();
        film.setGenres(distinct);
        genreStorage.add(film);
    }

    public void deleteGenre(Integer id) {
        genreStorage.delete(id);
    }

    public List<Genre> getFilmGenres(Integer filmId) {
        return new ArrayList<>(genreStorage.getFilmGenres(filmId));
    }

}