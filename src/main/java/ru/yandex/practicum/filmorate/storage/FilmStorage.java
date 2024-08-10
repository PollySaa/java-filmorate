package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {
    Film addFilm(Film film);

    void deleteFilm(int id);

    void updateFilm(Film film);

    Film getFilmById(int id);

    List<Film> getFilms();
}
