package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.LikeStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;

@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final LikeStorage likeStorage;
    private Film film;
    private final String errorUser = "Пользователь не найден";
    private final String errorFilm = "Фильм не найден";

    @Autowired
    public FilmService(@Qualifier("filmDbStorage") FilmStorage filmStorage,
                       @Qualifier("userDbStorage") UserStorage userStorage,
                       LikeStorage likeStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.likeStorage = likeStorage;
    }

    public void addLike(Integer userId, Integer filmId) {
        film = filmStorage.getFilmById(filmId);
        if (film != null) {
            if (userStorage.getUserById(userId) != null) {
                likeStorage.addLike(filmId, userId);
            } else {
                throw new NotFoundException(errorUser);
            }
        } else {
            throw new NotFoundException(errorFilm);
        }

    }

    public void removeLike(Integer userId, Integer filmId) {
        film = filmStorage.getFilmById(filmId);
        if (film != null) {
            if (film.getLikes().contains(userId)) {
                likeStorage.deleteLike(filmId, userId);
            } else {
                throw new NotFoundException(errorUser);
            }
        } else {
            throw new NotFoundException(errorFilm);
        }

    }

    public List<Film> getTopPopularFilms(Integer count) {
        if (count == null) {
            count = 10;
        }
        if (count < 1) {
            throw new ValidationException("Некорректное значение параметра count");
        }
        return likeStorage.getPopular(count);
    }

    public List<Film> getCommonFilms(Integer userId, Integer friendId) {
        if (userId.equals(friendId)) {
            throw new ValidationException("Попытка получить список общих фильмов для одного и того же пользователя");
        }
        return likeStorage.getCommonFilms(userId, friendId);
    }
}

