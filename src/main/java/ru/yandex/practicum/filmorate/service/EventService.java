package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Operation;
import ru.yandex.practicum.filmorate.storage.EventStorage;
import ru.yandex.practicum.filmorate.storage.LikeStorage;

import java.time.Instant;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class EventService {
    private final EventStorage eventStorage;
    private final LikeStorage likeStorage;

    public Event addEvent(final int userId,
                          final int entityId,
                          final EventType evenType,
                          final Operation operation) {
        Event event = Event.builder()
                .timestamp(Instant.now().toEpochMilli())
                .eventType(evenType)
                .operation(operation)
                .userId(userId)
                .entityId(entityId)
                .build();

        int id = eventStorage.addEvent(event);
        Event addedEvent = eventStorage.getEventById(id);
        log.info(addedEvent.toString());

        return addedEvent;
    }

    public List<Event> getUserFeed(final int userId) {
        likeStorage.checkContainsUserById(userId);

        return eventStorage.getEventsByUserId(userId);
    }
}