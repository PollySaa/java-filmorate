package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.EventService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserStorage userStorage;
    private final UserService userService;
    private final EventService eventService;

    @PostMapping
    public User addUser(@RequestBody User user) {
        log.info("Пришёл запрос на создание пользователя с email: {}", user.getEmail());
        validation(user);
        user = userStorage.addUser(user);
        return user;
    }

    @PutMapping("/{id}/friends/{friend-id}")
    public void addFriend(@PathVariable("id") Integer id, @PathVariable("friend-id") Integer friendId) {
        log.info("Пришёл запрос на добавление друга с id: {}", friendId);
        userService.addFriend(id, friendId);
    }

    @PutMapping
    public User updateUser(@RequestBody User user) {
        log.info("Пришёл запрос на обновление пользователя с email: {}", user.getEmail());
        validation(user);
        if (userStorage.getUserById(user.getId()) != null) {
            userStorage.updateUser(user);
            return user;
        } else {
            throw new NotFoundException("Пользователь с таким id: " + user.getId() + " не был найден!");
        }
    }

    @GetMapping
    public List<User> getAllUsers() {
        return userStorage.getUsers();
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable Integer id) {
        if (userStorage.getUserById(id) != null) {
            return userStorage.getUserById(id);
        } else {
            throw new NotFoundException("Пользователь с таким id: " + id + " не был найден!");
        }
    }

    @GetMapping("/{id}/friends")
    public List<User> getFriends(@PathVariable Integer id) {
        return userService.getFriends(id);
    }

    @GetMapping("/{id}/friends/common/{other-id}")
    public List<User> getCommonFriends(@PathVariable("id") Integer id, @PathVariable("other-id") Integer otherId) {
        return userService.getCommonFriends(id, otherId);
    }

    @DeleteMapping("/{id}/friends/{friend-id}")
    public void deleteFriend(@PathVariable("id") Integer id, @PathVariable("friend-id") Integer friendId) {
        log.info("Пришёл запрос на удаление друга с id: {}", friendId);
        userService.removeFriend(id, friendId);
    }

    @GetMapping("/{id}/recommendations")
    List<Film> getRecommendations(@PathVariable Integer id) {
        return userService.getRecommendations(id);
    }

    @DeleteMapping("/{user-id}")
    public void deleteUser(@PathVariable("user-id") Integer id) {
        log.info("Пришёл запрос на удаление пользователя с id: {}", id);
        userStorage.deleteUser(id);
    }

    @GetMapping("/{id}/feed")
    public List<Event> getUserFeed(@PathVariable Integer id) {
        return eventService.getUserFeed(id);
    }

    private void validation(User user) {
        String error;
        if (user.getLogin() == null || user.getLogin().isEmpty() || user.getLogin().contains(" ")) {
            error = "Логин не может быть пустым и содержать пробелы";
            log.error(error);
            throw new ValidationException(error);
        }

        if (user.getName() == null || user.getName().isEmpty()) {
            log.info("У пользователя отсутствует name, поэтому используется login");
            user.setName(user.getLogin());
        }

        if (user.getBirthday().isAfter(LocalDate.now())) {
            error = "Дата рождения не может быть в будущем";
            log.error(error);
            throw new ValidationException(error);
        }
    }
}
