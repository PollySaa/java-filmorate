package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Operation;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class EventDbStorage implements EventStorage {
    private final JdbcTemplate jdbcTemplate;
    private final UserStorage userStorage;
    private final NamedParameterJdbcOperations jdbcOperations;

    @Override
    public Event addEvent(Event event) {
        String sql = "INSERT INTO EVENTS(timestamp, user_id, event_type, operation, entity_id) " +
                "VALUES (:timestamp, :userId, :eventType, :operation, :entityId) ";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource map = new MapSqlParameterSource();
        map.addValue("timestamp", event.getTimestamp());
        map.addValue("userId", event.getUserId());
        map.addValue("eventType", event.getEventType().toString());
        map.addValue("operation", event.getOperation().toString());
        map.addValue("entityId", event.getEntityId());
        jdbcOperations.update(sql, map, keyHolder);
        event.setEventId(Objects.requireNonNull(keyHolder.getKey()).intValue());
        return event;
    }

    @Override
    public Event updateEvent(Event event) {
        String sql = "UPDATE EVENTS SET timestamp = :timestamp, user_id = :userID, event_type = :eventType, " +
                "operation = :operation, entity_id = :entityId WHERE event_id = :eventId";
        MapSqlParameterSource map = new MapSqlParameterSource();
        map.addValue("timestamp", event.getTimestamp());
        map.addValue("userId", event.getUserId());
        map.addValue("eventType", event.getEventType());
        map.addValue("operation", event.getOperation());
        map.addValue("eventId", event.getEventId());
        map.addValue("entityId", event.getEntityId());
        jdbcOperations.update(sql, map);
        return event;
    }

    @Override
    public Event getEventById(Integer eventId) {
        Event event;
        SqlRowSet eventRows = jdbcTemplate.queryForRowSet("SELECT * FROM events WHERE id = ?", eventId);
        if (eventRows.first()) {
            event = new Event(
                    eventRows.getLong("timestamp"),
                    eventRows.getInt("user_id"),
                    EventType.valueOf(eventRows.getString("event_type")),
                    Operation.valueOf(eventRows.getString("operation")),
                    eventRows.getInt("event_id"),
                    eventRows.getInt("entity_id")
            );
        } else {
            throw new NotFoundException("Событие с id = " + eventId + " не найдено!");
        }
        return event;
    }

    @Override
    public List<Event> getUserEventsById(Integer userId) {
        User user = userStorage.getUserById(userId);
        if (user != null) {
            String sql = "SELECT * FROM events WHERE user_id = ?";
            return jdbcTemplate.query(sql, (rs, rowNum) -> new Event(
                            rs.getLong("timestamp"),
                            rs.getInt("user_id"),
                            EventType.valueOf(rs.getString("event_type")),
                            Operation.valueOf(rs.getString("operation")),
                            rs.getInt("event_id"),
                            rs.getInt("entity_id")),
                    userId);
        } else {
            throw new NotFoundException("Пользователь с id " + userId + " не найден!");
        }
    }
}
