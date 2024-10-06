package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(makeFinal = false, level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class Genre {
    Integer id;
    @NotBlank
    String name;
}
