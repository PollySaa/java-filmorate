package ru.yandex.practicum.filmorate.service;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.FriendStorage;
import ru.yandex.practicum.filmorate.storage.LikeStorage;

import java.util.List;

@FieldDefaults(makeFinal = false, level = AccessLevel.PRIVATE)
@Service
public class UserService {
    final FriendStorage friendStorage;
    final LikeStorage likeStorage;
    final EventService eventService;
    Event event;

    public UserService(FriendStorage friendStorage, LikeStorage likeStorage, EventService eventService) {
        this.friendStorage = friendStorage;
        this.likeStorage = likeStorage;
        this.eventService = eventService;
    }

    public void addFriend(Integer userId, Integer friendId) {
        friendStorage.addFriend(userId, friendId);
        event = eventService.addEvent(userId, friendId, EventType.FRIEND, Operation.ADD);
    }

    public void removeFriend(Integer userId, Integer friendId) {
        friendStorage.deleteFriend(userId, friendId);
        event = eventService.addEvent(userId, friendId, EventType.FRIEND, Operation.REMOVE);
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
        return eventService.getUserFeed(userId);
    }
}
