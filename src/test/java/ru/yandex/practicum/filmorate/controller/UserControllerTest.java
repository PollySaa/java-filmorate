package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {
    private final UserController userController = new UserController();
    private User user;

    @BeforeEach
    public void beforeEach() {
        user = new User();
        user.setEmail("email@ysdf.com");
        user.setLogin("login");
        user.setName("name");
        user.setBirthday(LocalDate.of(2000, 7, 30));
    }

    public String setting(User user) {
        ValidationException exception = assertThrows(ValidationException.class, () -> userController.addUser(user));
        return exception.getMessage();
    }

    @Test
    public void emailValidationUser() {
        user.setEmail("dog");
        assertEquals("Электронная почта не может быть пустой и должна содержать символ @", setting(user));
    }

    @Test
    public void loginValidationUser() {
        user.setLogin("dog cat");
        assertEquals("Логин не может быть пустым и содержать пробелы", setting(user));
    }

    @Test
    public void nameValidationUser() {
        user.setName("");
        userController.addUser(user);
        assertEquals(user.getLogin(), user.getName());
    }

    @Test
    public void birthdayValidationUser() {
        user.setBirthday(LocalDate.of(2025, 7, 30));
        assertEquals("Дата рождения не может быть в будущем", setting(user));
    }
}