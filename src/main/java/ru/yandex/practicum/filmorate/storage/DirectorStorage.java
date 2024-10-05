package ru.yandex.practicum.filmorate.storage;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Director;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Repository
public class DirectorStorage {
    final JdbcTemplate jdbcTemplate;
    String query;
    String queryToDelete;

    @Autowired
    public DirectorStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Director> getDirectors() {
        query = "SELECT * FROM directors ;";
        return jdbcTemplate.query(query, this::mapRowToDirector);
    }

    public Director getDirectorById(Integer id) {
        query = "SELECT * FROM directors WHERE director_id = ? ;";
        return jdbcTemplate.queryForObject(query, this::mapRowToDirector, id);
    }

    public List<Director> getDirectorsByFilmId(Integer filmId) {
        query = "SELECT * FROM directors " +
                "WHERE director_id IN " +
                "(SELECT director_id FROM film_directors WHERE film_id = ?) ;";
        return jdbcTemplate.query(query, this::mapRowToDirector, filmId);
    }

    public Director addDirector(Director director) {
        SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("directors")
                .usingGeneratedKeyColumns("director_id");

        director.setId(insert.executeAndReturnKey(director.toMap()).intValue());

        return director;
    }

    public Director updateDirector(Director director) {
        query = "UPDATE directors SET name = ? WHERE director_id = ? ;";
        jdbcTemplate.update(query, director.getName(), director.getId());

        return director;
    }

    public Director deleteDirector(Integer id) {
        Director director = getDirectorById(id);
        query = "DELETE FROM directors WHERE director_id = ? ;";
        jdbcTemplate.update(query, id);

        return director;
    }

    public void putDirector(Integer directorId, Integer filmId) {
        queryToDelete = "DELETE FROM film_directors WHERE film_id = ? ;";
        jdbcTemplate.update(queryToDelete, filmId);

        String queryToUpdate = "INSERT INTO film_directors (director_id, film_id) VALUES (?, ?) ;";
        jdbcTemplate.update(queryToUpdate, directorId, filmId);
    }

    public void clearDirectors(Integer filmId) {
        queryToDelete = "DELETE FROM film_directors WHERE film_id = ? ;";
        jdbcTemplate.update(queryToDelete, filmId);
    }

    public boolean contains(Integer id) {
        query = "SELECT COUNT(*) FROM directors WHERE director_id = ? ;";
        Integer count = jdbcTemplate.queryForObject(query, Integer.class, id);

        return count > 0;
    }

    private Director mapRowToDirector(ResultSet rs, int rn) throws SQLException {
        return Director.builder()
                .id(rs.getInt("director_id"))
                .name(rs.getString("name"))
                .build();
    }
}
