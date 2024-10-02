package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.EventStorage;
import ru.yandex.practicum.filmorate.storage.FriendStorage;
import ru.yandex.practicum.filmorate.storage.LikeStorage;

import java.util.List;

@Service
public class UserService {
    private final FriendStorage friendStorage;
    private final LikeStorage likeStorage;
    private final EventStorage eventStorage;

    @Autowired
    public UserService(FriendStorage friendStorage, LikeStorage likeStorage, EventStorage eventStorage) {
        this.friendStorage = friendStorage;
        this.likeStorage = likeStorage;
        this.eventStorage = eventStorage;
    }

    public void addFriend(Integer userId, Integer friendId) {
        Event event =
                new Event(System.currentTimeMillis(), userId, EventType.FRIEND, Operation.ADD, null, friendId);
        eventStorage.addEvent(event);
        friendStorage.addFriend(userId, friendId);
    }

    public void removeFriend(Integer userId, Integer friendId) {
        friendStorage.deleteFriend(userId, friendId);
        Event event =
                new Event(System.currentTimeMillis(), userId, EventType.FRIEND, Operation.REMOVE, null, friendId);
        eventStorage.addEvent(event);
    }

    public List<User> getCommonFriends(Integer userId1, Integer userId2) {
        return friendStorage.getCommonFriends(userId1, userId2);
    }

    public List<User> getFriends(int userId) {
        return friendStorage.getFriends(userId);
    }

    public List<Film> getRecommendations(Integer id) {
        return likeStorage.getRecommendations(id);
    }

    public List<Event> getUserFeed(Integer userId) {
        return eventStorage.getUserEventsById(userId);
    }
}
