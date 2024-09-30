package ru.yandex.practicum.filmorate.service;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@FieldDefaults(makeFinal = false, level = AccessLevel.PRIVATE)
@AllArgsConstructor
@Service
public class DirectorService {
    DirectorStorage directorStorage;

    public List<Director> getDirectors() {
        return directorStorage.getDirectors();
    }

    public Director getDirectorById(Integer id) {
        if (!directorStorage.contains(id)) {
            throw new NotFoundException("По данному id режиссёр не найден");
        }
        return directorStorage.getDirectorById(id);
    }

    public Set<Director> getDirectorsByFilmId(Integer filmId) {
        return new HashSet<>(directorStorage.getDirectorsByFilmId(filmId));
    }

    public Director addDirector(Director director) {
        return directorStorage.addDirector(director);
    }

    public boolean exists(Integer id) {
        return directorStorage.contains(id);
    }

    public Director updateDirector(Director director) {
        if (!directorStorage.contains(director.getId())) {
            throw new NotFoundException("По данному id режиссёр не найден");
        }
        return directorStorage.updateDirector(director);
    }

    public Director deleteDirector(Integer id) {
        if (!directorStorage.contains(id)) {
            throw new NotFoundException("По данному id режиссёр не найден");
        }
        return directorStorage.deleteDirector(id);
    }

    public void putDirector(Integer directorId, Integer filmId) {
        if (!directorStorage.contains(directorId)) {
            throw new NotFoundException("По данному id режиссёр не найден");
        }
        directorStorage.putDirector(directorId, filmId);
    }
}
