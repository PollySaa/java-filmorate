package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class FilmControllerTest {
    private final FilmController filmController = new FilmController();
    private Film film;

    @BeforeEach
    public void beforeEach() {
        film = new Film();
        film.setName("name");
        film.setDescription("description");
        film.setReleaseDate(LocalDate.now());
        film.setDuration(120);
    }

    public String setting(Film film) {
        ValidationException exception = assertThrows(ValidationException.class, () -> filmController.addFilm(film));
        return exception.getMessage();
    }

    @Test
    public void nameValidateFilm() {
        film.setName("");
        assertEquals("Название не может быть пустым", setting(film));
    }

    @Test
    public void descriptionValidateFilm() {
        film.setDescription("11111111111111111111111111111111111111111111111111111111111111111111111111111111111111" +
                "111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111" +
                "111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111" +
                "111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111" +
                "111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111");
        assertEquals("Максимальная длина описания — 200 символов", setting(film));
    }

    @Test
    public void releaseDateValidationFilm() {
        film.setReleaseDate(LocalDate.of(1894, 7, 30));
        assertEquals("Дата релиза — не раньше 28 декабря 1895 года", setting(film));
    }

    @Test
    public void durationValidationFilm() {
        film.setDuration(-1);
        assertEquals("Продолжительность фильма должна быть положительным числом", setting(film));
    }
}