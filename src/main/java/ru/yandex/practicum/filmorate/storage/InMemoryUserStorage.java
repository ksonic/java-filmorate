package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class InMemoryUserStorage implements UserStorage {
    final Map<Long, User> users = new HashMap<>();
    protected int sequence = 0;

    public User createUser(User user) {
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
        return new ArrayList<>(users.values());
    }

    public List<User> getUsers() {
        return new ArrayList<>(users.values());
    }

    public User getUserById(long userId) {
        return users.get(userId);
    }

    public Boolean containsUser(long userId) {
        return users.containsKey(userId);
    }

    public void requestFriendship(long userId, long friendId) {
    }

    public List<Long> getUserFriends(long userId) {
        return null;
    }

    public void removeUserFromFriends(long userId, long friendId) {}
}
