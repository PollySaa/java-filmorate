package ru.yandex.practicum.filmorate.storage;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.GenreService;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.util.HashSet;
import java.util.List;

@FieldDefaults(makeFinal = false, level = AccessLevel.PRIVATE)
@Component
public class LikeStorage {
    final JdbcTemplate jdbcTemplate;
    final MpaService mpaService;
    final GenreService genreService;
    String sql;

    @Autowired
    public LikeStorage(JdbcTemplate jdbcTemplate, MpaService mpaService, GenreService genreService) {
        this.jdbcTemplate = jdbcTemplate;
        this.mpaService = mpaService;
        this.genreService = genreService;
    }

    public void addLike(Integer filmId, Integer userId) {
        sql = "INSERT INTO film_like (film_id, user_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, filmId, userId);
    }

    public void deleteLike(Integer filmId, Integer userId) {
        sql = "DELETE FROM film_like WHERE film_id = ? AND user_id = ?";
        jdbcTemplate.update(sql, filmId, userId);
    }

    public List<Film> getPopular(Integer count) {
        sql = "SELECT id, name, description, release_date, duration, rating_id " +
                "FROM film LEFT JOIN film_like ON film.id = film_like.film_id " +
                "GROUP BY film.id ORDER BY COUNT(film_like.user_id) DESC LIMIT ?";

        return jdbcTemplate.query(sql, (rs, rowNum) -> new Film(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getDate("release_Date").toLocalDate(),
                        rs.getInt("duration"),
                        new HashSet<>(getLikes(rs.getInt("id"))),
                        mpaService.getMpaById(rs.getInt("rating_id")),
                        genreService.getFilmGenres(rs.getInt("id"))),
                count);
    }

    public List<Integer> getLikes(Integer filmId) {
        sql = "SELECT user_id FROM film_like WHERE film_id = ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> rs.getInt("user_id"), filmId);
    }
}

