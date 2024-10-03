package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Event;

import java.util.List;

public interface EventStorage {
    Event addEvent(Event event);

    Event updateEvent(Event event);

    Event getEventById(Integer id);

    List<Event> getUserEventsById(Integer id);
}
