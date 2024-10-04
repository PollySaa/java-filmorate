package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Operation;
import ru.yandex.practicum.filmorate.storage.*;

import java.util.List;

@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final LikeStorage likeStorage;
    private final EventStorage eventStorage;
    private final String errorUser = "Пользователь не найден";
    private final String errorFilm = "Фильм не найден";
    private Film film;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage,
                       LikeStorage likeStorage, EventStorage eventStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.likeStorage = likeStorage;
        this.eventStorage = eventStorage;
    }

    public void addLike(Integer userId, Integer filmId) {
        film = filmStorage.getFilmById(filmId);
        if (film != null) {
            if (userStorage.getUserById(userId) != null) {

                if (!likeStorage.existsLike(filmId, userId)) {
                    likeStorage.addLike(filmId, userId);

                    Event event = new Event(System.currentTimeMillis(), userId, EventType.LIKE, Operation.ADD, null, filmId);
                    eventStorage.addEvent(event);
                }
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
        Event event =
                new Event(System.currentTimeMillis(), userId, EventType.LIKE, Operation.REMOVE, null, filmId);
        eventStorage.addEvent(event);
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

    public List<Film> getTopPopularFilmsByGenre(Integer genreId, Integer count) {
        if (count == null) {
            count = 10;
        }
        if (count < 1) {
            throw new ValidationException("Некорректное значение параметра count");
        }
        return likeStorage.getPopularByGenre(genreId, count);
    }

    public List<Film> getTopPopularFilmsByYear(Integer year, Integer count) {
        if (count == null) {
            count = 10;
        }
        if (count < 1) {
            throw new ValidationException("Некорректное значение параметра count");
        }
        return likeStorage.getPopularByYear(year, count);
    }

    public List<Film> getPopularByGenreAndYear(Integer genreId, Integer year, Integer count) {
        if (count == null || count < 1) {
            throw new ValidationException("Некорректное значение параметра count");
        }
        return likeStorage.getPopularByGenreAndYear(genreId, year, count);
    }

    public List<Film> getFilmsByDirector(Integer directorId, String sortBy) {
        return filmStorage.getFilmsByDirector(directorId, sortBy);
    }

    public List<Film> getCommonFilms(Integer userId, Integer friendId) {
        if (userId.equals(friendId)) {
            throw new ValidationException("Попытка получить список общих фильмов для одного и того же пользователя");
        }
        return likeStorage.getCommonFilms(userId, friendId);
    }

    public List<Film> searchFilms(String query, String by) {
        return filmStorage.searchFilms(query, by);
    }
}

