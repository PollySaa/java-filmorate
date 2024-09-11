package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FriendStorage;

import java.util.*;

@Service
public class UserService {
    private final FriendStorage friendStorage;

    @Autowired
    public UserService(FriendStorage friendStorage) {
        this.friendStorage = friendStorage;
    }

    public void addFriend(Integer userId, Integer friendId) {
        friendStorage.addFriend(userId, friendId);
    }

    public void removeFriend(Integer userId, Integer friendId) {
        friendStorage.deleteFriend(userId, friendId);
    }

    public List<User> getCommonFriends(Integer userId1, Integer userId2) {
        return friendStorage.getCommonFriends(userId1, userId2);
    }

    public List<User> getFriends(int userId) {
        return friendStorage.getFriends(userId);
    }
}
