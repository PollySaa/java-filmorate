package ru.yandex.practicum.filmorate.service;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Operation;
import ru.yandex.practicum.filmorate.storage.*;

import java.util.List;

@FieldDefaults(makeFinal = false, level = AccessLevel.PRIVATE)
@Service
public class FilmService {
    final FilmStorage filmStorage;
    final UserStorage userStorage;
    final LikeStorage likeStorage;
    final EventService eventService;
    final String errorUser = "Пользователь не найден";
    final String errorFilm = "Фильм не найден";
    final String errorCount = "Некорректное значение параметра count";
    Film film;
    Event event;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage, LikeStorage likeStorage,
                       EventService eventService) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.likeStorage = likeStorage;
        this.eventService = eventService;
    }

    public void addLike(Integer userId, Integer filmId) {
        film = filmStorage.getFilmById(filmId);
        if (film != null) {
            if (userStorage.getUserById(userId) != null) {

                if (!likeStorage.existsLike(filmId, userId)) {
                    likeStorage.addLike(filmId, userId);
                }
            } else {
                throw new NotFoundException(errorUser);
            }
        } else {
            throw new NotFoundException(errorFilm);
        }

        event = eventService.addEvent(userId, filmId, EventType.LIKE, Operation.ADD);
    }

    public void removeLike(Integer userId, Integer filmId) {
        film = filmStorage.getFilmById(filmId);
        if (film != null) {
            if (film.getLikes().contains(userId)) {
                likeStorage.deleteLike(filmId, userId);
                event = eventService.addEvent(userId, filmId, EventType.LIKE, Operation.REMOVE);
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
            throw new ValidationException(errorCount);
        }
        return likeStorage.getPopular(count);
    }

    public List<Film> getTopPopularFilmsByGenre(Integer genreId, Integer count) {
        if (count == null) {
            count = 10;
        }
        if (count < 1) {
            throw new ValidationException(errorCount);
        }
        return likeStorage.getPopularByGenre(genreId, count);
    }

    public List<Film> getTopPopularFilmsByYear(Integer year, Integer count) {
        if (count == null) {
            count = 10;
        }
        if (count < 1) {
            throw new ValidationException(errorCount);
        }
        return likeStorage.getPopularByYear(year, count);
    }

    public List<Film> getPopularByGenreAndYear(Integer genreId, Integer year, Integer count) {
        if (count == null || count < 1) {
            throw new ValidationException(errorCount);
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

