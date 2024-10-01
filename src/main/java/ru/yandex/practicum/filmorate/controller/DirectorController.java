package ru.yandex.practicum.filmorate.controller;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

import java.util.List;

@AllArgsConstructor
@FieldDefaults(makeFinal = false, level = AccessLevel.PRIVATE)
@RestController
public class DirectorController {
    DirectorService directorService;

    @GetMapping("/directors")
    public List<Director> getDirectors() {
        return directorService.getDirectors();
    }

    @GetMapping("/directors/{id}")
    public Director getDirectorById(@PathVariable Integer id) {
        return directorService.getDirectorById(id);
    }

    @PostMapping("/directors")
    public Director addDirector(@RequestBody Director director) {
        return directorService.addDirector(director);
    }

    @PutMapping("/directors")
    public Director updateDirector(@RequestBody Director director) {
        return directorService.updateDirector(director);
    }

    @DeleteMapping("/directors/{id}")
    public Director deleteDirector(@PathVariable Integer id) {
        return directorService.deleteDirector(id);
    }
}
