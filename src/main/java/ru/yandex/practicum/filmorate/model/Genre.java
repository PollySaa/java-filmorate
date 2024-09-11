package ru.yandex.practicum.filmorate.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(makeFinal = false, level = AccessLevel.PRIVATE)
public class Genre {
    Integer id;
    String name;

    public Genre(Integer id, String name) {
        this.id = id;
        this.name = name;
    }
}
