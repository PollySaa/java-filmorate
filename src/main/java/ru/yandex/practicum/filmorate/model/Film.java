package ru.yandex.practicum.filmorate.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.*;

@Data
@FieldDefaults(makeFinal = false, level = AccessLevel.PRIVATE)
public class Film {
    Integer id;
    String name;
    String description;
    LocalDate releaseDate;
    int duration;
    List<Genre> genres = new ArrayList<>();
    Mpa mpa;
    Set<Integer> likes = new HashSet<>();
    Set<Director> directors = new HashSet<>();

    public Film(Integer id, String name, String description, LocalDate releaseDate, Integer duration,
                Set<Integer> likes, Mpa mpa, List<Genre> genres, Set<Director> directors) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.likes = likes;
        this.mpa = mpa;
        this.genres = genres;
        this.directors = directors;
    }

    public Film() {
    }

    public Map<String, Object> toMap() {
        Map<String, Object> values = new HashMap<>();
        values.put("name", name);
        values.put("description", description);
        values.put("release_Date", releaseDate);
        values.put("duration", duration);
        values.put("rating_id", mpa.getId());
        return values;
    }
}
