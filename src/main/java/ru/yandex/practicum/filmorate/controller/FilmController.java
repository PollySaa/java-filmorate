package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private final Map<Integer, Film> films = new HashMap<>();
    private int generateId = 1;

    @PostMapping
    public Film addFilm(@RequestBody Film film) {
        log.info("Пришёл запрос на создание фильмя с именем {}", film.getName());
        validation(film);
        film.setId(generateId++);
        films.put(film.getId(), film);
        return film;
    }

    @PutMapping
    public Film updateFilm(@RequestBody Film film) {
        log.info("Пришёл запрос на обновление фильмя с именем {}", film.getName());
        validation(film);
        if (films.containsKey(film.getId())) {
            films.put(film.getId(), film);
            return film;
        } else {
            throw new ValidationException("Фильм с таким id" + film.getId() + "не был найден!");
        }
    }

    @GetMapping
    public List<Film> getAllFilms() {
        return new ArrayList<>(films.values());
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
