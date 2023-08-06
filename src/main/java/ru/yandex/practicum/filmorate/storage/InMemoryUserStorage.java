package ru.yandex.practicum.filmorate.storage;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Component
public class InMemoryUserStorage implements UserStorage {
    final Map<Long, User> users = new HashMap<>();
    protected int sequence = 0;

    public User createUser(User user) {
        if (users.containsKey(user.getId())) {
            throw new ValidationException("User with login " + user.getLogin() + " is already exists.");
        }
        if (!isBirthdayValid(user)) {
            throw new ValidationException("Validation error happened.");
        }
        useLoginAsName(user);
        user.setId(generateId());
        users.put(user.getId(), user);
        return user;
    }

    public void update(User user) {
        if (!users.containsKey(user.getId())) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "User with login " + user.getLogin() + " isn't exist."
            );
        }
        users.put(user.getId(), user);
    }

    private long generateId() {
        return ++sequence;
    }

    public List<User> getFilms() {
        return new LinkedList<>(users.values());
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

    private void useLoginAsName(User user) {
        if (user.getName() == null || user.getName().isEmpty()) {
            String login = user.getLogin();
            user.setName(login);
        }
    }

    public User getUserById(long userId) {
        if (!users.containsKey(userId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "User with id " + userId + " isn't exist."
            );
        }
        return users.get(userId);
    }

    public Map<Long, User> getUsers() {
        return users;
    }

}
