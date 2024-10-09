package ru.yandex.practicum.filmorate;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.core.AutoConfigureCache;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.UserDbStorage;

import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@AutoConfigureCache
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmorateApplicationTest {
    private final UserDbStorage userStorage;
    private final FilmDbStorage filmStorage;
    private final FilmService filmService;
    private final UserService userService;
    private User firstUser;
    private User secondUser;
    private User thirdUser;
    private Film firstFilm;
    private Film secondFilm;
    private Film thirdFilm;

    @BeforeEach
    public void beforeEach() {
        firstUser = new User();
        firstUser.setName("FirstUser");
        firstUser.setLogin("First");
        firstUser.setEmail("1@ya.ru");
        firstUser.setBirthday(LocalDate.of(1980, 12, 23));
        secondUser = new User();
        secondUser.setName("SecondUser");
        secondUser.setLogin("Second");
        secondUser.setEmail("2@ya.ru");
        secondUser.setBirthday(LocalDate.of(1980, 12, 24));
        thirdUser = new User();
        thirdUser.setName("ThirdUser");
        thirdUser.setLogin("Third");
        thirdUser.setEmail("3@ya.ru");
        thirdUser.setBirthday(LocalDate.of(1980, 12, 25));
        firstFilm = new Film();
        firstFilm.setName("Something1");
        firstFilm.setDescription("Empty");
        firstFilm.setReleaseDate(LocalDate.of(1960, 5, 5));
        firstFilm.setDuration(120);
        firstFilm.setMpa(new Mpa(1, "G"));
        firstFilm.setLikes(new HashSet<>());

        secondFilm = new Film();
        secondFilm.setName("Something2");
        secondFilm.setDescription("Empty");
        secondFilm.setReleaseDate(LocalDate.of(2000, 7, 7));
        secondFilm.setDuration(160);
        secondFilm.setMpa(new Mpa(3, "PG-13"));
        secondFilm.setLikes(new HashSet<>());

        thirdFilm = new Film();
        thirdFilm.setName("Something3");
        thirdFilm.setDescription("Empty");
        thirdFilm.setReleaseDate(LocalDate.of(1970, 10, 10));
        thirdFilm.setDuration(130);
        thirdFilm.setLikes(new HashSet<>());
        thirdFilm.setMpa(new Mpa(4, "R"));
    }

    @Test
    public void testAddUserAndGetUserById() {
        firstUser = userStorage.addUser(firstUser);
        Optional<User> userOptional = Optional.ofNullable(userStorage.getUserById(firstUser.getId()));
        assertThat(userOptional)
                .hasValueSatisfying(user ->
                        assertThat(user)
                                .hasFieldOrPropertyWithValue("id", firstUser.getId())
                                .hasFieldOrPropertyWithValue("name", "FirstUser"));
    }

    @Test
    public void testUpdateUser() {
        firstUser = userStorage.addUser(firstUser);
        User newUser = new User(firstUser.getId(), "new2@ya.ru", "Second", "NewFirstUser",
                LocalDate.of(2000, 7, 7), null);
        Optional<User> testUpdateUser = Optional.ofNullable(userStorage.updateUser(newUser));
        assertThat(testUpdateUser)
                .hasValueSatisfying(user -> assertThat(user)
                        .hasFieldOrPropertyWithValue("name", "NewFirstUser")
                );
    }

    @Test
    public void deleteUser() {
        firstUser = userStorage.addUser(firstUser);
        userStorage.deleteUser(firstUser.getId());
        List<User> listUsers = userStorage.getUsers();
        assertThat(listUsers).hasSize(0);
    }

    @Test
    public void testGetUsers() {
        firstUser = userStorage.addUser(firstUser);
        secondUser = userStorage.addUser(secondUser);
        List<User> listUsers = userStorage.getUsers();
        assertThat(listUsers).hasSize(2);
    }

    @Test
    public void testAddFilmAndGetFilmById() {
        firstFilm = filmStorage.addFilm(firstFilm);
        Optional<Film> filmOptional = Optional.ofNullable(filmStorage.getFilmById(firstFilm.getId()));
        assertThat(filmOptional)
                .hasValueSatisfying(film -> assertThat(film)
                        .hasFieldOrPropertyWithValue("id", firstFilm.getId())
                        .hasFieldOrPropertyWithValue("name", "Something1")
                );
    }

    @Test
    public void testUpdateFilm() {
        firstFilm = filmStorage.addFilm(firstFilm);
        Film updateFilm = new Film(firstFilm.getId(), "new1", "NewEmpty",
                LocalDate.of(2000, 7, 7), 120, null, new Mpa(1, "G"),
                null, Set.of());
        updateFilm.setMpa(new Mpa(1, "G"));
        Optional<Film> testUpdateFilm = Optional.ofNullable(filmStorage.updateFilm(updateFilm));
        assertThat(testUpdateFilm)
                .hasValueSatisfying(film ->
                        assertThat(film)
                                .hasFieldOrPropertyWithValue("name", "new1")
                                .hasFieldOrPropertyWithValue("description", "NewEmpty")
                );
    }

    @Test
    public void deleteFilm() {
        firstFilm = filmStorage.addFilm(firstFilm);
        filmStorage.deleteFilm(firstFilm.getId());
        List<Film> listFilms = filmStorage.getFilms();
        assertThat(listFilms).hasSize(0);
    }

    @Test
    public void testGetFilms() {
        firstFilm = filmStorage.addFilm(firstFilm);
        secondFilm = filmStorage.addFilm(secondFilm);
        List<Film> listFilms = filmStorage.getFilms();
        assertThat(listFilms).hasSize(2);
    }

    @Test
    public void testAddLike() {
        firstUser = userStorage.addUser(firstUser);
        firstFilm = filmStorage.addFilm(firstFilm);
        filmService.addLike(firstUser.getId(), firstFilm.getId());
        firstFilm = filmStorage.getFilmById(firstFilm.getId());
        assertThat(firstFilm.getLikes()).hasSize(1);
        assertThat(firstFilm.getLikes()).contains(firstUser.getId());
    }

    @Test
    public void testDeleteLike() {
        firstUser = userStorage.addUser(firstUser);
        secondUser = userStorage.addUser(secondUser);
        firstFilm = filmStorage.addFilm(firstFilm);
        filmService.addLike(firstUser.getId(), firstFilm.getId());
        filmService.addLike(secondUser.getId(), firstFilm.getId());
        filmService.removeLike(firstUser.getId(), firstFilm.getId());
        firstFilm = filmStorage.getFilmById(firstFilm.getId());
        assertThat(firstFilm.getLikes()).hasSize(1);
        assertThat(firstFilm.getLikes()).contains(secondUser.getId());
    }

    @Test
    public void testGetPopularFilms() {

        firstUser = userStorage.addUser(firstUser);
        secondUser = userStorage.addUser(secondUser);
        thirdUser = userStorage.addUser(thirdUser);

        firstFilm = filmStorage.addFilm(firstFilm);
        filmService.addLike(firstUser.getId(), firstFilm.getId());

        secondFilm = filmStorage.addFilm(secondFilm);
        filmService.addLike(firstUser.getId(), secondFilm.getId());
        filmService.addLike(secondUser.getId(), secondFilm.getId());
        filmService.addLike(thirdUser.getId(), secondFilm.getId());

        thirdFilm = filmStorage.addFilm(thirdFilm);
        filmService.addLike(firstUser.getId(), thirdFilm.getId());
        filmService.addLike(secondUser.getId(), thirdFilm.getId());

        List<Film> listFilms = filmService.getTopPopularFilms(5);

        assertThat(listFilms).hasSize(3);

        assertThat(Optional.of(listFilms.get(0)))
                .hasValueSatisfying(film ->
                        AssertionsForClassTypes.assertThat(film)
                                .hasFieldOrPropertyWithValue("name", "Something2"));

        assertThat(Optional.of(listFilms.get(1)))
                .hasValueSatisfying(film ->
                        AssertionsForClassTypes.assertThat(film)
                                .hasFieldOrPropertyWithValue("name", "Something3"));

        assertThat(Optional.of(listFilms.get(2)))
                .hasValueSatisfying(film ->
                        AssertionsForClassTypes.assertThat(film)
                                .hasFieldOrPropertyWithValue("name", "Something1"));
    }

    @Test
    public void testGetCommonFilms() {
        firstUser = userStorage.addUser(firstUser);
        secondUser = userStorage.addUser(secondUser);
        thirdUser = userStorage.addUser(thirdUser);

        firstFilm = filmStorage.addFilm(firstFilm);
        filmService.addLike(firstUser.getId(), firstFilm.getId());
        filmService.addLike(secondUser.getId(), firstFilm.getId());

        secondFilm = filmStorage.addFilm(secondFilm);
        filmService.addLike(firstUser.getId(), secondFilm.getId());
        filmService.addLike(secondUser.getId(), secondFilm.getId());
        filmService.addLike(thirdUser.getId(), secondFilm.getId());

        thirdFilm = filmStorage.addFilm(thirdFilm);
        filmService.addLike(firstUser.getId(), thirdFilm.getId());

        List<Film> listFilms = filmService.getCommonFilms(firstUser.getId(), secondUser.getId());

        assertThat(listFilms).hasSize(2);

        assertThat(Optional.of(listFilms.get(0)))
                .hasValueSatisfying(film ->
                        AssertionsForClassTypes.assertThat(film)
                                .hasFieldOrPropertyWithValue("name", "Something2"));

        assertThat(Optional.of(listFilms.get(1)))
                .hasValueSatisfying(film ->
                        AssertionsForClassTypes.assertThat(film)
                                .hasFieldOrPropertyWithValue("name", "Something1"));
    }

    @Test
    public void testAddFriend() {
        firstUser = userStorage.addUser(firstUser);
        secondUser = userStorage.addUser(secondUser);
        userService.addFriend(firstUser.getId(), secondUser.getId());
        assertThat(userService.getFriends(firstUser.getId())).hasSize(1);
    }

    @Test
    public void testDeleteFriend() {
        firstUser = userStorage.addUser(firstUser);
        secondUser = userStorage.addUser(secondUser);
        thirdUser = userStorage.addUser(thirdUser);
        userService.addFriend(firstUser.getId(), secondUser.getId());
        userService.addFriend(firstUser.getId(), thirdUser.getId());
        userService.removeFriend(firstUser.getId(), secondUser.getId());
        assertThat(userService.getFriends(firstUser.getId())).hasSize(1);
    }

    @Test
    public void testGetFriends() {
        firstUser = userStorage.addUser(firstUser);
        secondUser = userStorage.addUser(secondUser);
        thirdUser = userStorage.addUser(thirdUser);
        userService.addFriend(firstUser.getId(), secondUser.getId());
        userService.addFriend(firstUser.getId(), thirdUser.getId());
        assertThat(userService.getFriends(firstUser.getId())).hasSize(2);
    }

    @Test
    public void testGetCommonFriends() {
        firstUser = userStorage.addUser(firstUser);
        secondUser = userStorage.addUser(secondUser);
        thirdUser = userStorage.addUser(thirdUser);
        userService.addFriend(firstUser.getId(), secondUser.getId());
        userService.addFriend(firstUser.getId(), thirdUser.getId());
        userService.addFriend(secondUser.getId(), firstUser.getId());
        userService.addFriend(secondUser.getId(), thirdUser.getId());
        assertThat(userService.getCommonFriends(firstUser.getId(), secondUser.getId())).hasSize(1);
    }
}