package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
        userStorage.requestFriendship(userId, friendId);
    }

    public void deleteUserFromFriends(long userId, long friendId) {
        User user = userStorage.getUserById(userId);
        user.getFriendIds().remove(friendId);
        userStorage.removeUserFromFriends(userId, friendId);
    }

    public List<User> getUserFriends(long userId) {
        if (!userStorage.containsUser(userId)) {
            throw new NotFoundException("User with id" + userId + " is not found.");
        }
        if (userStorage.getUserFriends(userId).isEmpty()) {
            return new ArrayList<>();
        }
        return userStorage.getUserById(userId).getFriendIds().stream().map(userStorage::getUserById).collect(Collectors.toList());
    }

    public List<User> getCommonFriends(long userId, long otherId) {
        if (!userStorage.containsUser(userId)) {
            throw new NotFoundException("User with id" + userId + " is not found.");
        }
        if (!userStorage.containsUser(otherId)) {
            throw new NotFoundException("User with id" + otherId + " is not found.");
        }
        Set<Long> firstUserFriends = userStorage.getUserById(userId).getFriendIds();
        Set<Long> secondUserFriends = userStorage.getUserById(otherId).getFriendIds();
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

    public User addUser(User user) throws SQLException {
        if (userStorage.containsUser(user.getId())) {
            throw new ValidationException("User with login " + user.getLogin() + " is already exists.");
        }
        if (!validateBirthday(user)) {
            throw new ValidationException("Validation error happened.");
        }
        useLoginAsName(user);
        return userStorage.createUser(user);
    }

    public User updateUser(User user) {
        if (!userStorage.containsUser(user.getId())) {
            throw new NotFoundException("User with id" + user.getId() + " is not found.");
        }
        return userStorage.update(user);
    }

    public List<User> getUsers() {
        return new ArrayList<>(userStorage.getUsers());
    }

    public User getUserById(long userId) {
        if (!userStorage.containsUser(userId)) {
            throw new NotFoundException("User with id" + userId + " is not found.");
        }
        return userStorage.getUserById(userId);
    }

    private boolean validateBirthday(User user) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Date docDate = format.parse(user.getBirthday());
            Date now = new Date();
            return docDate.before(now);
        } catch (Exception exception) {
            throw new ValidationException("Error happened: ", exception);
        }
    }

    private void useLoginAsName(User user) {
        if (user.getName() == null || user.getName().isEmpty()) {
            String login = user.getLogin();
            user.setName(login);
        }
    }
}
