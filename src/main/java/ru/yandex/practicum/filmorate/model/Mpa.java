package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder(toBuilder = true)
@FieldDefaults(makeFinal = false, level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class Mpa {
    Integer id;
    @NotBlank
    String name;
}
