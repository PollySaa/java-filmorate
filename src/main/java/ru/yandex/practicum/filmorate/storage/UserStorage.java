package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {
    User addUser(User user);

    void deleteUser(int id);

    void updateUser(User user);

    User getUserById(Integer id);

    List<User> getUsers();
}
