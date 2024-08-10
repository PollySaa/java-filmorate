package ru.yandex.practicum.filmorate.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@FieldDefaults(makeFinal = false, level = AccessLevel.PRIVATE)
public class User {
    Integer id;
    String email;
    String login;
    String name;
    LocalDate birthday;
    Set<Integer> friends = new HashSet<>();

    public void addFriend(int id) {
        friends.add(id);
    }

    public void removeFriend(int id) {
        friends.remove(id);
    }
}
