package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Map;

public interface UserStorage {
    User createUser(User user);

    void update(User user);

    List<User> getFilms();

    User getUserById(long userId);

    Map<Long, User> getUsers();

}
