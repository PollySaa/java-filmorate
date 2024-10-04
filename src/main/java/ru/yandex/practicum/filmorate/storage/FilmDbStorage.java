package ru.yandex.practicum.filmorate.storage;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.DirectorService;
import ru.yandex.practicum.filmorate.service.GenreService;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Component("filmDbStorage")
public class FilmDbStorage implements FilmStorage {
    final JdbcTemplate jdbcTemplate;
    final MpaService mpaService;
    final LikeStorage likeStorage;
    final GenreService genreService;
    final DirectorService directorService;
    Film film;
    String sql;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate, MpaService mpaService, LikeStorage likeStorage,
                         GenreService genreService, DirectorService directorService) {
        this.jdbcTemplate = jdbcTemplate;
        this.mpaService = mpaService;
        this.likeStorage = likeStorage;
        this.genreService = genreService;
        this.directorService = directorService;
    }

    @Override
    public Film addFilm(Film film) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("film")
                .usingGeneratedKeyColumns("id");

        film.setId(simpleJdbcInsert.executeAndReturnKey(film.toMap()).intValue());
        film.setMpa(mpaService.getMpaById(film.getMpa().getId()));

        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            for (Genre genre : film.getGenres()) {
                genre.setName(genreService.getGenreById(genre.getId()).getName());
            }
            genreService.putGenres(film);
        }

        if (!film.getDirectors().isEmpty()) {
            for (Director director : film.getDirectors()) {
                if (!directorService.exists(director.getId())) {
                    throw new NotFoundException("По данному id режиссёр не найден");
                }
                director.setName(directorService.getDirectorById(director.getId()).getName());
                directorService.putDirector(director.getId(), film.getId());
            }
        }

        return film;
    }

    @Override
    public void deleteFilm(Integer id) {
        film = getFilmById(id);
        sql = "DELETE FROM film WHERE id = ?";
        if (jdbcTemplate.update(sql, id) == 0) {
            throw new NotFoundException("Фильм с id = " + id + " не найден!");
        }
    }

    @Override
    public Film updateFilm(Film film) {
        sql = "UPDATE film SET name = ?, description = ?, release_date = ?, duration = ?, rating_id = ? WHERE id = ?";
        int updatedRows = jdbcTemplate.update(
                sql,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId()
        );
        if (updatedRows == 0) {
            throw new NotFoundException("Фильм с id = " + film.getId() + " не найден для обновления!");
        }

        genreService.deleteGenre(film.getId());
        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            for (Genre genre : film.getGenres()) {
                genre.setName(genreService.getGenreById(genre.getId()).getName());
            }
            genreService.putGenres(film);
        }

        if (!film.getDirectors().isEmpty()) {
            for (Director director : film.getDirectors()) {
                if (!directorService.exists(director.getId())) {
                    throw new NotFoundException("По данному id режиссёр не найден");
                }
                director.setName(directorService.getDirectorById(director.getId()).getName());
                directorService.putDirector(director.getId(), film.getId());
            }
        } else {
            directorService.clearDirectors(film.getId());
        }

        return film;
    }

    @Override
    public Film getFilmById(Integer id) {
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet("SELECT * FROM film WHERE id = ?", id);
        if (filmRows.first()) {
            Mpa mpa = mpaService.getMpaById(filmRows.getInt("rating_id"));
            List<Genre> genres = genreService.getFilmGenres(id);
            film = new Film(
                    filmRows.getInt("id"),
                    filmRows.getString("name"),
                    filmRows.getString("description"),
                    Objects.requireNonNull(filmRows.getDate("release_date")).toLocalDate(),
                    filmRows.getInt("duration"),
                    new HashSet<>(likeStorage.getLikes(filmRows.getInt("id"))),
                    mpa,
                    genres,
                    directorService.getDirectorsByFilmId(filmRows.getInt("id")));

        } else {
            throw new NotFoundException("Фильм с id = " + id + " не найден!");
        }
        return film;
    }

    @Override
    public List<Film> getFilms() {
        sql = "SELECT * FROM film";
        return jdbcTemplate.query(sql, (rs, rowNum) -> new Film(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("description"),
                rs.getDate("release_date").toLocalDate(),
                rs.getInt("duration"),
                new HashSet<>(likeStorage.getLikes(rs.getInt("id"))),
                mpaService.getMpaById(rs.getInt("rating_id")),
                genreService.getFilmGenres(rs.getInt("id")),
                directorService.getDirectorsByFilmId(rs.getInt("id"))
        ));
    }

    @Override
    public List<Film> getFilmsByDirector(Integer directorId, String sortBy) {

        if (!directorService.exists(directorId)) {
            throw new NotFoundException("По данному id режиссёр не найден");
        }

        List<Film> orderedFilms = new ArrayList<>();

        if (sortBy.equals("year")) {
            sql = "SELECT * FROM film " +
                    "WHERE id IN " +
                    "(SELECT film_id FROM film_directors WHERE director_id = ?) " +
                    "ORDER BY release_date ;";
            orderedFilms.addAll(jdbcTemplate.query(sql, this::mapRowToFilm, directorId));
        } else if (sortBy.equals("likes")) {
            sql = "SELECT id, name, description, release_date, duration, rating_id, COUNT(fl.film_id) AS likes_count FROM film AS f " +
                    "JOIN film_like AS fl ON f.id = fl.film_id " +
                    "WHERE f.id IN " +
                    "(SELECT film_id FROM film_directors WHERE director_id = ?) " +
                    "GROUP BY f.id " +
                    "ORDER BY likes_count DESC ";
            orderedFilms.addAll(jdbcTemplate.query(sql, this::mapRowToFilm, directorId));
        } else {
            throw new ValidationException("Неверный параметр запроса. Возможны варианты: [year, like]");
        }
        return orderedFilms;
    }

    @Override
    public boolean contains(Integer id) {
        sql = "SELECT COUNT(*) FROM film WHERE id = ? ;";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, id);

        return count > 0;
    }

    @Override
    public List<Film> searchFilms(String query, String by) {
        String lowerQuery = query.toLowerCase();

        if (by.equals("title")) {
            sql = "SELECT * FROM film WHERE LOWER(name) LIKE ? ;";

            return jdbcTemplate.query(sql, new String[]{"%" + lowerQuery + "%"}, this::mapRowToFilm);

        } else if (by.equals("director")) {
            sql = "SELECT * FROM film " +
                    "WHERE id IN " +
                    "(SELECT film_id FROM film_directors WHERE director_id IN " +
                    "(SELECT director_id FROM directors WHERE LOWER(name) LIKE ?)) ;";

            return jdbcTemplate.query(sql, new String[]{"%" + lowerQuery + "%"}, this::mapRowToFilm);

        } else if (by.equals("director,title") || by.equals("title,director")) {
            sql = "SELECT * FROM film " +
                    "WHERE id IN " +
                    "(SELECT film_id FROM film_directors WHERE director_id IN " +
                    "(SELECT director_id FROM directors WHERE LOWER(name) LIKE ?))" +
                    "OR LOWER(name) LIKE ? ;";

            return jdbcTemplate.query(sql, new String[]{"%" + lowerQuery + "%", "%" + lowerQuery + "%"}, this::mapRowToFilm);

        } else {
            throw new ValidationException("Поиск осуществляется по критериям director и/или title");
        }
    }

    private Film mapRowToFilm(ResultSet rs, int rn) throws SQLException {
        return new Film(rs.getInt("id"),
                rs.getString("name"),
                rs.getString("description"),
                rs.getDate("release_date").toLocalDate(),
                rs.getInt("duration"),
                new HashSet<>(likeStorage.getLikes(rs.getInt("id"))),
                mpaService.getMpaById(rs.getInt("rating_id")),
                genreService.getFilmGenres(rs.getInt("id")),
                directorService.getDirectorsByFilmId(rs.getInt("id")));
    }
}
