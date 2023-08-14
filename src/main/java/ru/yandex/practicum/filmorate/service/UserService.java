package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.ArrayList;
import java.util.LinkedList;
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
        if (!containsUser(userId)) {
            throw new NotFoundException("User with id" + userId + " is not found.");
        }
        if (!containsUser(friendId)) {
            throw new NotFoundException("User with id" + friendId + " is not found.");
        }
        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);
        user.addFriend(friendId);
        friend.addFriend(userId);
        userStorage.getUsers().put(userId, user);
        userStorage.getUsers().put(friendId, friend);
    }

    public void deleteUserFromFriends(long userId, long friendId) {
        User user = userStorage.getUserById(userId);
        userStorage.getUserById(userId);
        userStorage.getUserById(friendId);
        user.getFriends().remove(friendId);
        userStorage.getUsers().put(user.getId(), user);
    }

    public List<User> getUserFriends(long userId) {
        if (!containsUser(userId)) {
            throw new NotFoundException("User with id" + userId + " is not found.");
        }
        return userStorage.getUserById(userId).getFriends().stream().map(userStorage::getUserById).collect(Collectors.toList());
    }

    public List<User> getCommonFriends(long userId, long otherId) {
        if (!containsUser(userId)) {
            throw new NotFoundException("User with id" + userId + " is not found.");
        }
        if (!containsUser(otherId)) {
            throw new NotFoundException("User with id" + otherId + " is not found.");
        }
        Set<Long> firstUserFriends = userStorage.getUserById(userId).getFriends();
        Set<Long> secondUserFriends = userStorage.getUserById(otherId).getFriends();
        List<User> fullParamFriends = new ArrayList<>();
        if (firstUserFriends == null || secondUserFriends == null) {
            return fullParamFriends;
        }
        List<Long> commonFriendsId = firstUserFriends
                .stream()
                .filter(secondUserFriends::contains)
                .distinct()
                .collect(Collectors.toList());
        for (Long friend : commonFriendsId) {
            fullParamFriends.add(userStorage.getUserById(friend));
        }
        return fullParamFriends;
    }

    public List<User> getUsers() {
        return new LinkedList<>(userStorage.getUsers().values());
    }

    public void addUser(User user) {
        if (containsUser(user.getId())) {
            throw new ValidationException("User with login " + user.getLogin() + " is already exists.");
        }
        userStorage.createUser(user);
    }

    public void updateUser(User user) {
        if (!containsUser(user.getId())) {
            throw new NotFoundException("User with id" + user.getId() + " is not found.");
        }
        userStorage.update(user);
    }

    public User getUserById(long userId) {
        if (!containsUser(userId)) {
            throw new NotFoundException("User with id" + userId + " is not found.");
        }
        return userStorage.getUserById(userId);
    }

    public boolean containsUser(long userId) {
        return userStorage.getUsers().containsKey(userId);
    }
}
