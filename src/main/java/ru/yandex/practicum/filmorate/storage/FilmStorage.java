package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {
    Film addFilm(Film film);

    Film deleteFilm(Integer id);

    Film updateFilm(Film film);

    Film getFilmById(Integer id);

    List<Film> getFilms();

    List<Film> getFilmsByDirector(Integer directorId, String sortBy);

    boolean contains(Integer id);

    List<Film> searchFilms(String query, String by);
}
