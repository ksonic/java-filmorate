package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Service
public class UserService {
    private final UserStorage userStorage;

    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public void addUserToFriends(long userId, long friendId) {
        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);
        if (!userStorage.getUsers().containsKey(userId)) {
            throw new ValidationException("User with id " + userId + " is not found.");
        }
        if (!userStorage.getUsers().containsKey(friendId)) {
            throw new ValidationException("User with id " + friendId + " is not found.");
        }
        user.setFriend(friendId);
        friend.setFriend(userId);
        userStorage.getUsers().put(userId, user);
        userStorage.getUsers().put(friendId, friend);
    }

    public void deleteUserFromFriends(long userId, long friendId) {
        User user = userStorage.getUserById(userId);
        if (!userStorage.getUsers().containsKey(user.getId())) {
            throw new ValidationException("User with id " + userId + " is not found.");
        }
        if (!userStorage.getUsers().containsKey(friendId)) {
            throw new ValidationException("User with id " + friendId + " is not found.");
        }
        user.getFriends().remove(friendId);
        userStorage.getUsers().put(user.getId(), user);
    }

    public List<User> getUserFriends(long userId) {
        if (!userStorage.getUsers().containsKey(userId)) {
            throw new ValidationException("User with id " + userId + " is not found.");
        }
        return userStorage.getUserById(userId).getFriends().stream().map(userStorage::getUserById).collect(Collectors.toList());
    }

    public List<User> getCommonFriends(long userId, long otherId) {
        if (!userStorage.getUsers().containsKey(userId)) {
            throw new ValidationException("User with id " + userId + " is not found.");
        }
        if (!userStorage.getUsers().containsKey(otherId)) {
            throw new ValidationException("User with id " + otherId + " is not found.");
        }

        Set<Long> firstUserFriends = userStorage.getUserById(userId).getFriends();
        Set<Long> secondUserFriends = userStorage.getUserById(otherId).getFriends();
        List<User> fullParamFriends = new ArrayList<>();
        if (firstUserFriends == null || secondUserFriends == null) {
            return fullParamFriends;
        }
        List<Long> commonFriendsId = firstUserFriends.stream().filter(secondUserFriends::contains).distinct().collect(Collectors.toList());
        for (Long friend : commonFriendsId) {
            fullParamFriends.add(userStorage.getUserById(friend));
        }
        return fullParamFriends;
    }
}
