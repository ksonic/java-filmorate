package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {
    User createUser(User user);

    User update(User user);

    List<User> getFilms();

    List<User> getUsers();

    User getUserById(long userId);

    Boolean containsUser(long userId);

}
