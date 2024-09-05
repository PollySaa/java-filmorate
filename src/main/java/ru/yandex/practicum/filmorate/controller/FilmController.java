package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private final FilmStorage filmStorage;
    private final FilmService filmService;

    @Autowired
    public FilmController(FilmStorage filmStorage, FilmService filmService) {
        this.filmStorage = filmStorage;
        this.filmService = filmService;
    }

    @PostMapping
    public Film addFilm(@RequestBody Film film) {
        log.info("Пришёл запрос на создание фильма с именем: {}", film.getName());
        validation(film);
        filmStorage.addFilm(film);
        return film;
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable Integer id, @PathVariable Integer userId) {
        filmService.addLike(userId, id);
    }

    @PutMapping
    public Film updateFilm(@RequestBody Film film) {
        log.info("Пришёл запрос на обновление фильмя с именем: {}", film.getName());
        validation(film);
        if (filmStorage.getFilmById(film.getId()) != null) {
            filmStorage.updateFilm(film);
            return film;
        } else {
            throw new NotFoundException("Фильм с таким id: " + film.getId() + " не был найден!");
        }
    }

    @GetMapping
    public List<Film> getAllFilms() {
        return filmStorage.getFilms();
    }

    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable Integer id) {
        return filmStorage.getFilmById(id);
    }

    @GetMapping("/popular")
    public List<Film> getPopular(@RequestParam(value = "count", required = false) Integer count) {
        if (count == null) {
            count = 10;
        }
        return filmService.getTopPopularFilms(count);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable Integer id, @PathVariable Integer userId) {
        filmService.removeLike(userId, id);
    }

    private void validation(Film film) {
        String error;
        if (film.getName() == null || film.getName().isEmpty()) {
            error = "Название не может быть пустым";
            log.error(error);
            throw new ValidationException(error);
        }

        if (film.getDescription() != null && film.getDescription().length() > 200) {
            error = "Максимальная длина описания — 200 символов";
            log.error(error);
            throw new ValidationException(error);
        }

        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            error = "Дата релиза — не раньше 28 декабря 1895 года";
            log.error(error);
            throw new ValidationException(error);
        }

        if (film.getDuration() <= 0) {
            error = "Продолжительность фильма должна быть положительным числом";
            log.error(error);
            throw new ValidationException(error);
        }
    }
}
