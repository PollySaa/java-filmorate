package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

@Component
public class MpaStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public MpaStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Mpa> getAllMpa() {
        String sql = "SELECT * FROM mpa";
        return jdbcTemplate.query(sql, (rs, rowNum) -> new Mpa(
                rs.getInt("id"),
                rs.getString("name")
        ));
    }

    public Mpa getMpaById(Integer id) {
        Mpa mpa;
        SqlRowSet mpaRows = jdbcTemplate.queryForRowSet("SELECT * FROM mpa WHERE id = ?", id);
        if (mpaRows.first()) {
            mpa = new Mpa(
                    mpaRows.getInt("id"),
                    mpaRows.getString("name")
            );
        } else {
            throw new NotFoundException("Рейтинг с id = " + id + " не найден!");
        }
        return mpa;
    }
}
