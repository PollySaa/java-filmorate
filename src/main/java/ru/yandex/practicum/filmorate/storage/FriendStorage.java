package ru.yandex.practicum.filmorate.storage;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Component
public class FriendStorage {
    final JdbcTemplate jdbcTemplate;
    final UserStorage userStorage;
    final String error = "Пользователь не найден";
    User user;
    User friend;
    String sql;

    @Autowired
    public FriendStorage(JdbcTemplate jdbcTemplate, @Qualifier("userDbStorage") UserStorage userStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.userStorage = userStorage;
    }

    public void addFriend(Integer userId, Integer friendId) {
        check(userId, friendId);

        if (user != null && friend != null) {
            if (friend.getFriends().contains(userId)) {
                sql = "UPDATE friends SET status = ? WHERE user_id = ? AND friend_id = ?";
                jdbcTemplate.update(sql, true, friendId, userId);
            } else {
                sql = "INSERT INTO friends (user_id, friend_id, status) VALUES (?, ?, ?)";
                jdbcTemplate.update(sql, userId, friendId, true);
            }
        } else {
            throw new NotFoundException(error);
        }
    }

    public void deleteFriend(Integer userId, Integer friendId) {
        check(userId, friendId);

        if ((user != null) && (friend != null)) {
            sql = "DELETE FROM friends WHERE user_id = ? AND friend_id = ?";
            jdbcTemplate.update(sql, userId, friendId);
            if (friend.getFriends().contains(userId)) {
                sql = "UPDATE friends SET user_id = ? AND friend_id = ? AND status = ? " +
                        "WHERE user_id = ? AND friend_id = ?";
                jdbcTemplate.update(sql, friendId, userId, false, friendId, userId);
            }
        } else {
            throw new NotFoundException(error);
        }
    }

    public List<User> getFriends(Integer userId) {
        user = userStorage.getUserById(userId);
        if (user != null) {
            sql = "SELECT friend_id, email, login, name, birthday FROM friends" +
                    " INNER JOIN users ON friends.friend_id = users.id WHERE friends.user_id = ?";
            return jdbcTemplate.query(sql, (rs, rowNum) -> new User(
                            rs.getInt("friend_id"),
                            rs.getString("email"),
                            rs.getString("login"),
                            rs.getString("name"),
                            rs.getDate("birthday").toLocalDate(),
                            null),
                    userId
            );
        } else {
            throw new NotFoundException(error);
        }
    }

    public List<User> getCommonFriends(Integer userId1, Integer userId2) {
        check(userId1, userId2);

        if (user != null && friend != null) {
            sql = "SELECT u.* FROM users u " +
                    "JOIN friends f1 ON u.id = f1.friend_id " +
                    "JOIN friends f2 ON f2.friend_id = u.id " +
                    "WHERE f1.user_id = ? AND f2.user_id = ? AND f1.status = true AND f2.status = true";

            return jdbcTemplate.query(sql, (rs, rowNum) -> new User(
                            rs.getInt("id"),
                            rs.getString("email"),
                            rs.getString("login"),
                            rs.getString("name"),
                            rs.getDate("birthday").toLocalDate(),
                            null),
                    userId1, userId2
            );
        } else {
            throw new NotFoundException(error);
        }
    }

    public void check(int userId, int friendId) {
        user = userStorage.getUserById(userId);
        friend = userStorage.getUserById(friendId);
    }
}

