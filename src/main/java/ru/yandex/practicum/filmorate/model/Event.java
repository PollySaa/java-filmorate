package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(makeFinal = false, level = AccessLevel.PRIVATE)
@AllArgsConstructor
@Builder(toBuilder = true)
public class Event {
    Long timestamp;
    @NotBlank
    Integer userId;
    @NotBlank
    EventType eventType;
    @NotBlank
    Operation operation;
    Integer eventId;
    @NotNull
    Integer entityId;
}
