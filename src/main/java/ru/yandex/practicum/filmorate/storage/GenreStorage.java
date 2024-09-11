package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

@Component
public class GenreStorage {
    private final JdbcTemplate jdbcTemplate;
    private String sql;

    @Autowired
    public GenreStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Genre> getGenres() {
        sql = "SELECT * FROM genre";
        return jdbcTemplate.query(sql, (rs, rowNum) -> new Genre(
                rs.getInt("id"),
                rs.getString("name"))
        );
    }

    public Genre getGenreById(Integer id) {
        Genre genre;
        SqlRowSet genreRows = jdbcTemplate.queryForRowSet("SELECT * FROM genre WHERE id = ?", id);
        if (genreRows.first()) {
            genre = new Genre(
                    genreRows.getInt("id"),
                    genreRows.getString("name")
            );
        } else {
            throw new NotFoundException("Жанр с id = " + id + " не найден!");
        }
        return genre;
    }

    public void delete(Film film) {
        jdbcTemplate.update("DELETE FROM film_genre WHERE film_id = ?", film.getId());
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
        sql = "SELECT genre_id, name FROM film_genre" +
                " INNER JOIN genre ON genre_id = id WHERE film_id = ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> new Genre(
                rs.getInt("genre_id"),
                rs.getString("name")), filmId
        );
    }
}
