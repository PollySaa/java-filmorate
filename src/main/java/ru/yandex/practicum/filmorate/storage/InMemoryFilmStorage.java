package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Integer, Film> films = new HashMap<>();
    private Integer id = 1;

    @Override
    public Film addFilm(Film film) {
        if (film.getId() != null) {
            throw new ValidationException("Id фильма должен быть пустым при добавлении");
        }
        film.setId(id++);
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public void deleteFilm(int id) {
        if (films.containsKey(id)) {
            films.remove(id);
        } else {
            throw new ValidationException("Фильм с таким id " + id + " не был найден!");
        }
    }

    @Override
    public void updateFilm(Film film) {
        if (film.getId() == null) {
            throw new ValidationException("Id фильма не может быть пустым");
        }
        if (films.containsKey(film.getId())) {
            films.put(film.getId(), film);
        } else {
            throw new ValidationException("Фильм с таким id " + film.getId() + " не был найден!");
        }
    }

    @Override
    public Film getFilmById(int id) {
        return films.get(id);
    }

    @Override
    public List<Film> getFilms() {
        return new ArrayList<>(films.values());
    }
}
