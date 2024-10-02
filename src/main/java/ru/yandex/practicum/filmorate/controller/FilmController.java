package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController {
    private final FilmStorage filmStorage;
    private final FilmService filmService;

    @PostMapping
    public Film addFilm(@RequestBody Film film) {
        log.info("Пришёл запрос на создание фильма с именем: {}", film.getName());
        validation(film);
        filmStorage.addFilm(film);
        return film;
    }

    @PutMapping("/{id}/like/{user-id}")
    public void addLike(@PathVariable("id") Integer id, @PathVariable("user-id") Integer userId) {
        filmService.addLike(userId, id);
    }

    @PutMapping
    public Film updateFilm(@RequestBody Film film) {
        log.info("Пришёл запрос на обновление фильма с именем: {}", film.getName());
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
    public List<Film> getPopular(
            @RequestParam(value = "count", required = false) Integer count,
            @RequestParam(value = "genreId", required = false) Integer genreId,
            @RequestParam(value = "year", required = false) Integer year
    ) {
        Integer validCount = checkCount(count);

        if (genreId != null && year != null) {
            return filmService.getPopularByGenreAndYear(genreId, year, validCount);
        }

        if (genreId != null) {
            return filmService.getTopPopularFilmsByGenre(genreId, validCount);
        }

        if (year != null) {
            return filmService.getTopPopularFilmsByYear(year, validCount);
        }

        return filmService.getTopPopularFilms(validCount);
    }

    private Integer checkCount(Integer count) {
        if (count == null) {
            return 10; // Значение по умолчанию
        }
        return count;
    }

    @GetMapping("/director/{directorId}")
    public List<Film> getFilmsByDirector(@PathVariable Integer directorId, @RequestParam String sortBy) {
        return filmService.getFilmsByDirector(directorId, sortBy);
    }

    @DeleteMapping("/{id}/like/{user-id}")
    public void deleteLike(@PathVariable("id") Integer id, @PathVariable("user-id") Integer userId) {
        filmService.removeLike(userId, id);
    }

    @DeleteMapping("/{film-id}")
    public void deleteFilm(@PathVariable("film-id") Integer id) {
        log.info("Пришёл запрос на удаление фильма с id: {}", id);
        filmStorage.deleteFilm(id);
    }

    @GetMapping("/common")
    public List<Film> getCommonFilms(@RequestParam Integer userId, @RequestParam Integer friendId) {
        return filmService.getCommonFilms(userId, friendId);
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

        if (film.getMpa().getId() > 5) {
            error = "id возрастного рейтинга не может быть больше 5";
            log.error(error);
            throw new ValidationException(error);
        }

        for (Genre genre : film.getGenres()) {
            if (genre.getId() > 6) {
                error = "id жанра не может быть больше 6";
                log.error(error);
                throw new ValidationException(error);
            }
        }
    }
}
