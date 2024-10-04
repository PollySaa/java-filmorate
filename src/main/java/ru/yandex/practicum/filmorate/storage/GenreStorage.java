package ru.yandex.practicum.filmorate.storage;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Component
public class GenreStorage {
    final JdbcTemplate jdbcTemplate;
    final GenreMapper genreMapper;
    String sql;

    @Autowired
    public GenreStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.genreMapper = new GenreMapper();
    }

    public List<Genre> getGenres() {
        sql = "SELECT * FROM genre";
        return jdbcTemplate.query(sql, genreMapper);
    }

    public Genre getGenreById(Integer id) {
        sql = "SELECT * FROM genre WHERE id = ?";
        List<Genre> genres = jdbcTemplate.query(sql, genreMapper, id);
        if (genres.isEmpty()) {
            throw new NotFoundException("Жанр с id = " + id + " не найден!");
        }
        return genres.getFirst();
    }

    public void delete(Integer id) {
        jdbcTemplate.update("DELETE FROM film_genre WHERE film_id = ?", id);
    }

    public void add(Film film) {
        if (film.getGenres() != null) {
            for (Genre genre : film.getGenres()) {
                jdbcTemplate.update("INSERT INTO film_genre (film_id, genre_id) VALUES (?, ?)",
                        film.getId(), genre.getId());
            }
        }
    }

    public List<Genre> getFilmGenres(Integer filmId) {
        sql = "SELECT * FROM genre WHERE id IN (SELECT genre_id FROM film_genre WHERE film_id = ?)";
        return jdbcTemplate.query(sql, genreMapper, filmId);
    }
}