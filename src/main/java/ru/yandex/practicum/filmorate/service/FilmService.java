package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private Film film;
    private User user;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public void addLike(Integer userId, Integer filmId) {
        if (userId == null) {
            throw new NotFoundException("Пользователь не найден");
        }
        if (filmId == null) {
            throw new NotFoundException("Фильм не найден");
        }
        film = filmStorage.getFilmById(filmId);
        if (film == null) {
            throw new NotFoundException("Фильм не найден");
        }
        user = userStorage.getUserById(userId);
        if (user == null) {
            throw new NotFoundException("Пользователь не найден");
        }
        if (film.hasLikeFrom(userId)) {
            throw new ValidationException("Пользователь уже поставил лайк");
        }
        film.addLike(userId);
        filmStorage.updateFilm(film);
    }

    public void removeLike(Integer userId, Integer filmId) {
        if (userId == null) {
            throw new NotFoundException("Пользователь не найден");
        }
        if (filmId == null) {
            throw new NotFoundException("Фильм не найден");
        }
        film = filmStorage.getFilmById(filmId);
        if (film == null) {
            throw new NotFoundException("Фильм не найден");
        }
        user = userStorage.getUserById(userId);
        if (user == null) {
            throw new NotFoundException("Пользователь не найден");
        }
        if (!film.hasLikeFrom(userId)) {
            throw new ValidationException("Пользователь не ставил лайк");
        }
        film.removeLike(userId);
        filmStorage.updateFilm(film);
    }

    public List<Film> getTopPopularFilms(Integer count) {
        if (count == null) {
            count = 10;
        }
        if (count < 1) {
            throw new ValidationException("Некорректное значение параметра count");
        }
        return filmStorage.getFilms().stream()
                .sorted(Comparator.comparingInt(Film::getLikesCount).reversed())
                .limit(count)
                .collect(Collectors.toList());
    }
}

