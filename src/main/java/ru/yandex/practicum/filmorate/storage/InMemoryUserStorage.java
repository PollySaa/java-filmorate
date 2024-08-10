package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Integer, User> users = new HashMap<>();
    private Integer id = 1;

    @Override
    public User addUser(User user) {
        if (user.getId() != null) {
            throw new ValidationException("Id пользователя должен быть пустым при добавлении");
        }
        user.setId(id++);
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public void deleteUser(int id) {
        if (users.containsKey(id)) {
            users.remove(id);
        } else {
            throw new NotFoundException("Пользователь с таким id: " + id + " не был найден!");
        }
    }

    @Override
    public void updateUser(User user) {
        if (user.getId() == null) {
            throw new ValidationException("Id пользователя не может быть пустым");
        }
        if (users.containsKey(user.getId())) {
            users.put(user.getId(), user);
        } else {
            throw new NotFoundException("Пользователь с таким id: " + user.getId() + " не был найден!");
        }
    }

    @Override
    public User getUserById(Integer id) {
        return users.get(id);
    }

    @Override
    public List<User> getUsers() {
        return new ArrayList<>(users.values());
    }
}
