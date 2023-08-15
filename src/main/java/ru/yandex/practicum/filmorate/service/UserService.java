package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
        if (!userStorage.containsUser(userId)) {
            throw new NotFoundException("User with id" + userId + " is not found.");
        }
        if (!userStorage.containsUser(friendId)) {
            throw new NotFoundException("User with id" + friendId + " is not found.");
        }
        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);
        user.addFriendId(friendId);
        friend.addFriendId(userId);
        userStorage.getUsers().add(user);
        userStorage.getUsers().add(friend);
    }

    public void deleteUserFromFriends(long userId, long friendId) {
        User user = userStorage.getUserById(userId);
        userStorage.getUserById(userId);
        userStorage.getUserById(friendId);
        user.getFriendsId().remove(friendId);
        userStorage.getUsers().add(user);
    }

    public List<User> getUserFriends(long userId) {
        if (!userStorage.containsUser(userId)) {
            throw new NotFoundException("User with id" + userId + " is not found.");
        }
        return userStorage.getUserById(userId).getFriendsId().stream().map(userStorage::getUserById).collect(Collectors.toList());
    }

    public List<User> getCommonFriends(long userId, long otherId) {
        if (!userStorage.containsUser(userId)) {
            throw new NotFoundException("User with id" + userId + " is not found.");
        }
        if (!userStorage.containsUser(otherId)) {
            throw new NotFoundException("User with id" + otherId + " is not found.");
        }
        Set<Long> firstUserFriends = userStorage.getUserById(userId).getFriendsId();
        Set<Long> secondUserFriends = userStorage.getUserById(otherId).getFriendsId();
        List<User> fullParamFriends = new ArrayList<>();
        if (firstUserFriends == null || secondUserFriends == null) {
            return fullParamFriends;
        }
        List<Long> commonFriendIds = firstUserFriends
                .stream()
                .filter(secondUserFriends::contains)
                .distinct()
                .collect(Collectors.toList());
        for (Long friend : commonFriendIds) {
            fullParamFriends.add(userStorage.getUserById(friend));
        }
        return fullParamFriends;
    }

    public User addUser(User user) {
        if (userStorage.containsUser(user.getId())) {
            throw new ValidationException("User with login " + user.getLogin() + " is already exists.");
        }
        if (!isBirthdayValid(user)) {
            throw new ValidationException("Validation error happened.");
        }
        return userStorage.createUser(user);
    }

    public void updateUser(User user) {
        if (!userStorage.containsUser(user.getId())) {
            throw new NotFoundException("User with id" + user.getId() + " is not found.");
        }
        userStorage.update(user);
    }

    public List<User> getUsers() {
        return new LinkedList<>(userStorage.getUsers());
    }

    public User getUserById(long userId) {
        if (!userStorage.containsUser(userId)) {
            throw new NotFoundException("User with id" + userId + " is not found.");
        }
        return userStorage.getUserById(userId);
    }

    private boolean isBirthdayValid(User user) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Date docDate = format.parse(user.getBirthday());
            Date now = new Date();
            return docDate.before(now);
        } catch (Exception exception) {
            throw new ValidationException("Error happened: ", exception);
        }
    }
}
