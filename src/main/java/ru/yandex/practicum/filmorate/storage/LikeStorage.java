package ru.yandex.practicum.filmorate.storage;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.DirectorService;
import ru.yandex.practicum.filmorate.service.GenreService;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.util.HashSet;
import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Component
public class LikeStorage {
    final JdbcTemplate jdbcTemplate;
    final MpaService mpaService;
    final GenreService genreService;
    final DirectorService directorService;
    String sql;

    @Autowired
    public LikeStorage(JdbcTemplate jdbcTemplate, MpaService mpaService, GenreService genreService, DirectorService directorService) {
        this.jdbcTemplate = jdbcTemplate;
        this.mpaService = mpaService;
        this.genreService = genreService;
        this.directorService = directorService;
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
                        genreService.getFilmGenres(rs.getInt("id")),
                        directorService.getDirectorsByFilmId(rs.getInt("id"))),
                count);
    }

    public List<Film> getPopularByGenre(Integer genreId, Integer count) {
        String sql = "SELECT f.id, f.name, f.description, f.release_date, f.duration, f.rating_id " +
                "FROM film f " +
                "JOIN film_genre fg ON f.id = fg.film_id " +
                "LEFT JOIN film_like fl ON f.id = fl.film_id " +
                "WHERE fg.genre_id = ? " +
                "GROUP BY f.id " +
                "ORDER BY COUNT(fl.user_id) DESC " +
                "LIMIT ?";

        return jdbcTemplate.query(sql, (rs, rowNum) -> new Film(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getDate("release_Date").toLocalDate(),
                        rs.getInt("duration"),
                        new HashSet<>(getLikes(rs.getInt("id"))),
                        mpaService.getMpaById(rs.getInt("rating_id")),
                        genreService.getFilmGenres(rs.getInt("id")),
                        directorService.getDirectorsByFilmId(rs.getInt("id"))),
                genreId, count);
    }

    public List<Film> getPopularByYear(Integer year, Integer count) {
        String sql = "SELECT f.id, f.name, f.description, f.release_date, f.duration, f.rating_id " +
                "FROM film f " +
                "LEFT JOIN film_like fl ON f.id = fl.film_id " +
                "WHERE EXTRACT(YEAR FROM f.release_date) = ? " +
                "GROUP BY f.id " +
                "ORDER BY COUNT(fl.user_id) DESC " +
                "LIMIT ?";

        return jdbcTemplate.query(sql, (rs, rowNum) -> new Film(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("description"),
                rs.getDate("release_Date").toLocalDate(),
                rs.getInt("duration"),
                new HashSet<>(getLikes(rs.getInt("id"))),
                mpaService.getMpaById(rs.getInt("rating_id")),
                genreService.getFilmGenres(rs.getInt("id")),
                directorService.getDirectorsByFilmId(rs.getInt("id"))),
                year, count);
    }

    public List<Film> getPopularByGenreAndYear(Integer genreId, Integer year, Integer count) {
        String sql = "SELECT f.id, f.name, f.description, f.release_date, f.duration, f.rating_id " +
                "FROM film f " +
                "JOIN film_genre fg ON f.id = fg.film_id " +
                "LEFT JOIN film_like fl ON f.id = fl.film_id " +
                "WHERE fg.genre_id = ? AND EXTRACT(YEAR FROM f.release_date) = ? " +
                "GROUP BY f.id " +
                "ORDER BY COUNT(fl.user_id) DESC " +
                "LIMIT ?";

        return jdbcTemplate.query(sql, (rs, rowNum) -> new Film(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("description"),
                rs.getDate("release_Date").toLocalDate(),
                rs.getInt("duration"),
                new HashSet<>(getLikes(rs.getInt("id"))),
                mpaService.getMpaById(rs.getInt("rating_id")),
                genreService.getFilmGenres(rs.getInt("id")),
                directorService.getDirectorsByFilmId(rs.getInt("id"))),
                genreId, year, count);
    }

    public List<Integer> getLikes(Integer filmId) {
        sql = "SELECT user_id FROM film_like WHERE film_id = ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> rs.getInt("user_id"), filmId);
    }

    public List<Film> getCommonFilms(Integer userId, Integer friendId) {
        checkContainsUserById(userId);
        checkContainsUserById(friendId);

        String sqlForFindCommonFilmsIds = "SELECT film_like.film_id " +
                "FROM film_like " +
                "WHERE film_like.user_id = ? AND film_like.film_id IN (" +
                "SELECT film_like.film_id " +
                "FROM film_like " +
                "WHERE film_like.user_id = ?)";

        sql = "SELECT film.* " +
                "FROM film LEFT JOIN film_like ON film.id = film_like.film_id " +
                "WHERE film_like.film_id IN (" + sqlForFindCommonFilmsIds + ")" +
                "GROUP BY film.id ORDER BY COUNT(film_like.user_id) DESC";

        return jdbcTemplate.query(sql, (rs, rowNum) -> new Film(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getDate("release_Date").toLocalDate(),
                        rs.getInt("duration"),
                        new HashSet<>(getLikes(rs.getInt("id"))),
                        mpaService.getMpaById(rs.getInt("rating_id")),
                        genreService.getFilmGenres(rs.getInt("id")),
                        directorService.getDirectorsByFilmId(rs.getInt("id"))),
                userId, friendId);
    }

    public List<Film> getRecommendations(Integer id) {
        checkContainsUserById(id);
        List<Film> films;
        sql = "SELECT film.* FROM film " +
                "WHERE film.id IN (" +
                "SELECT film_id FROM film_like " +
                "WHERE user_id IN (" +
                "SELECT FL1.user_id FROM film_like FL1 " +
                "RIGHT JOIN film_like FL2 ON FL2.film_id = FL1.film_id " +
                "GROUP BY FL1.user_id, FL2.user_id " +
                "HAVING FL1.user_id IS NOT NULL AND " +
                "FL1.user_id <> ? AND FL2.user_id = ? " +
                "ORDER BY COUNT(FL1.user_id) DESC " +
                "LIMIT 1" +
                ") " +
                "AND film_id NOT IN (" +
                "SELECT film_id FROM film_like WHERE user_id = ?" +
                ")" +
                ")";
        films = jdbcTemplate.query(sql, (rs, rowNum) -> new Film(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getDate("release_Date").toLocalDate(),
                        rs.getInt("duration"),
                        new HashSet<>(getLikes(rs.getInt("id"))),
                        mpaService.getMpaById(rs.getInt("rating_id")),
                        genreService.getFilmGenres(rs.getInt("id")),
                        directorService.getDirectorsByFilmId(rs.getInt("id"))),
                id, id, id);
        return films;
    }

    public boolean existsLike(Integer filmId, Integer userId) {
        String sql = "SELECT COUNT(*) FROM film_like WHERE film_id = ? AND user_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, filmId, userId);
        return count != null && count > 0;
    }

    public void checkContainsUserById(Integer userId) {
        if (userId == null) {
            throw new ValidationException("Передан пустой аргумент!");
        }
        String sql = "SELECT COUNT(id) FROM users WHERE id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, userId);
        if (count == null || count == 0) {
            throw new NotFoundException("Пользователь с id = " + userId + " не найден!");
        }
    }
}