package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;

@Service
public class UserService {
    private final UserStorage userStorage;
    private User user;
    private User friend;
    private final String error = "Пользователь не найден";

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public void addFriend(Integer userId, Integer friendId) {
        check(userId, friendId);

        if (user != null && friend != null) {
            user.addFriend(friendId);
            friend.addFriend(userId);

        } else {
            throw new NotFoundException(error);
        }
    }

    public void removeFriend(Integer userId, Integer friendId) {
        check(userId, friendId);

        if (user != null && friend != null) {
            user.removeFriend(friendId);
            friend.removeFriend(userId);
        } else {
            throw new NotFoundException(error);
        }
    }

    public List<User> getCommonFriends(Integer userId1, Integer userId2) {
        check(userId1, userId2);

        if (user != null && friend != null) {
            Set<Integer> commonFriendsIds = new HashSet<>(user.getFriends());
            commonFriendsIds.retainAll(friend.getFriends());

            List<User> commonFriends = new ArrayList<>();
            for (Integer friendId : commonFriendsIds) {
                User friend = userStorage.getUserById(friendId);
                if (friend != null) {
                    commonFriends.add(friend);
                }
            }
            return commonFriends;
        } else {
            throw new NotFoundException(error);
        }
    }

    public List<User> getFriends(int userId) {
        user = userStorage.getUserById(userId);

        if (user != null) {
            List<User> friends = new ArrayList<>();
            for (Integer friendId : user.getFriends()) {
                User friend = userStorage.getUserById(friendId);
                if (friend != null) {
                    friends.add(friend);
                }
            }
            return friends;
        } else {
            throw new NotFoundException(error);
        }
    }

    public void check(int userId, int friendId) {
        user = userStorage.getUserById(userId);
        friend = userStorage.getUserById(friendId);
    }
}
