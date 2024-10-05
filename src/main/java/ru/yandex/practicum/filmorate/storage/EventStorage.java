package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Event;

import java.util.List;

public interface EventStorage {
    int addEvent(final Event event);

    List<Event> getEventsByUserId(final int userId);

    Event getEventById(final int id);
}