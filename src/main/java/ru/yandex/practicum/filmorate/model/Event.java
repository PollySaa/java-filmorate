package ru.yandex.practicum.filmorate.model;

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
    Integer userId;
    EventType eventType;
    Operation operation;
    Integer eventId;
    Integer entityId;
}
