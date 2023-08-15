package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Component
public class InMemoryUserStorage implements UserStorage {
    final Map<Long, User> users = new HashMap<>();
    protected int sequence = 0;

    public User createUser(User user) {
        useLoginAsName(user);
        user.setId(generateId());
        users.put(user.getId(), user);
        return user;
    }

    public User update(User user) {
        users.put(user.getId(), user);
        return user;
    }

    private long generateId() {
        return ++sequence;
    }

    public List<User> getFilms() {
        return new LinkedList<>(users.values());
    }


    private void useLoginAsName(User user) {
        if (user.getName() == null || user.getName().isEmpty()) {
            String login = user.getLogin();
            user.setName(login);
        }
    }

    public List<User> getUsers() {
        return new LinkedList<>(users.values());
    }

    public User getUserById(long userId) {
        return users.get(userId);
    }

    public Boolean containsUser(long userId) {
        return users.containsKey(userId);
    }

}
